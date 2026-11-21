package com.farm.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.farm.common.BusinessException;
import com.farm.common.ErrorCode;
import com.farm.common.PageResult;
import com.farm.enums.DictType;
import com.farm.entity.Category;
import com.farm.entity.Channel;
import com.farm.entity.ClickLog;
import com.farm.entity.CostType;
import com.farm.entity.Product;
import com.farm.entity.SalesRecord;
import com.farm.entity.Shop;
import com.farm.entity.SysDict;
import com.farm.entity.SysUser;
import com.farm.mapper.CategoryMapper;
import com.farm.mapper.ChannelMapper;
import com.farm.mapper.ClickLogMapper;
import com.farm.mapper.CostTypeMapper;
import com.farm.mapper.ProductMapper;
import com.farm.mapper.SalesRecordMapper;
import com.farm.mapper.ShopMapper;
import com.farm.mapper.SysDictMapper;
import com.farm.mapper.SysUserMapper;
import com.farm.security.LoginUser;
import com.farm.security.UserContext;
import com.farm.vo.AdminClickStatsVO;
import com.farm.vo.AdminOverviewVO;
import com.farm.vo.ClickStatItemVO;
import com.farm.vo.DictTypeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 后台管理服务（仅管理员）
 */
@Service
@RequiredArgsConstructor
public class AdminService {

    private final SysUserMapper userMapper;
    private final ShopMapper shopMapper;
    private final ProductMapper productMapper;
    private final SalesRecordMapper salesMapper;
    private final CategoryMapper categoryMapper;
    private final ChannelMapper channelMapper;
    private final CostTypeMapper costTypeMapper;
    private final SysDictMapper dictMapper;
    private final ClickLogMapper clickLogMapper;

    private void requireAdmin() {
        LoginUser user = UserContext.get();
        if (user == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        if (user.getRole() == null || user.getRole() != 3) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }

    /**
     * 平台整体统计
     */
    public AdminOverviewVO overview() {
        requireAdmin();
        AdminOverviewVO vo = new AdminOverviewVO();
        vo.setUserCount(userMapper.selectCount(null));
        vo.setShopCount(shopMapper.selectCount(new LambdaQueryWrapper<Shop>().eq(Shop::getStatus, 1)));
        vo.setPendingShopCount(shopMapper.selectCount(new LambdaQueryWrapper<Shop>().eq(Shop::getStatus, 0)));
        vo.setProductCount(productMapper.selectCount(null));
        vo.setSalesCount(salesMapper.selectCount(null));
        BigDecimal totalAmount = salesMapper.selectList(null).stream()
                .map(SalesRecord::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTotalSalesAmount(totalAmount);
        return vo;
    }

    /**
     * 商家列表（按审核状态筛选，分页）
     */
    public PageResult<Shop> listShops(Integer status, long pageNum, long pageSize) {
        requireAdmin();
        Page<Shop> page = new Page<>(pageNum, pageSize);
        shopMapper.selectPage(page, new LambdaQueryWrapper<Shop>()
                .eq(status != null, Shop::getStatus, status)
                .orderByDesc(Shop::getCreateTime));
        return PageResult.of(page);
    }

    /**
     * 审核商家
     */
    public void auditShop(Long id, Boolean pass, String reason) {
        requireAdmin();
        Shop shop = shopMapper.selectById(id);
        if (shop == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        boolean approve = pass != null && pass;
        shop.setStatus(approve ? 1 : 2);
        shop.setRejectReason(approve ? null : reason);
        shopMapper.updateById(shop);
        if (approve) {
            SysUser user = userMapper.selectById(shop.getUserId());
            if (user != null && (user.getRole() == null || user.getRole() != 3)) {
                user.setRole(2);
                userMapper.updateById(user);
            }
        }
    }

    /**
     * 用户列表（密码脱敏，分页）
     */
    public PageResult<SysUser> listUsers(Integer role, String phone, long pageNum, long pageSize) {
        requireAdmin();
        boolean hasPhone = phone != null && !phone.isBlank();
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        userMapper.selectPage(page, new LambdaQueryWrapper<SysUser>()
                .eq(role != null, SysUser::getRole, role)
                .eq(hasPhone, SysUser::getPhone, phone)
                .orderByDesc(SysUser::getCreateTime));
        page.getRecords().forEach(u -> u.setPassword(null));
        return PageResult.of(page);
    }

    /**
     * 禁用/启用用户（管理员不可被禁用）
     */
    public void updateUserStatus(Long id, Integer status) {
        requireAdmin();
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        if (user.getRole() != null && user.getRole() == 3) {
            throw new BusinessException(ErrorCode.FORBIDDEN.getCode(), "不能禁用管理员");
        }
        user.setStatus(status);
        userMapper.updateById(user);
    }

    /**
     * 产品列表（按状态筛选，分页）
     */
    public PageResult<Product> listProducts(Integer status, long pageNum, long pageSize) {
        requireAdmin();
        Page<Product> page = new Page<>(pageNum, pageSize);
        productMapper.selectPage(page, new LambdaQueryWrapper<Product>()
                .eq(status != null, Product::getStatus, status)
                .orderByDesc(Product::getCreateTime));
        return PageResult.of(page);
    }

    /**
     * 产品状态变更（如强制下架）
     */
    public void updateProductStatus(Long id, Integer status) {
        requireAdmin();
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        product.setStatus(status);
        productMapper.updateById(product);
    }

    /**
     * 设置/取消产品推荐（推荐位，C6：由管理员在后台设置）
     *
     * @param id          产品ID
     * @param isRecommend 1-推荐 0-取消
     */
    public void updateProductRecommend(Long id, Integer isRecommend) {
        requireAdmin();
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        product.setIsRecommend(isRecommend == null ? 0 : isRecommend);
        productMapper.updateById(product);
    }

    // ============ 分类管理（字典，全量返回，不分页） ============

    public List<Category> listCategories() {
        requireAdmin();
        return categoryMapper.selectList(new LambdaQueryWrapper<Category>().orderByAsc(Category::getSort));
    }

    public Category createCategory(Category category) {
        requireAdmin();
        category.setId(null);
        categoryMapper.insert(category);
        return category;
    }

    public Category updateCategory(Long id, Category category) {
        requireAdmin();
        if (categoryMapper.selectById(id) == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        category.setId(id);
        categoryMapper.updateById(category);
        return category;
    }

    public void deleteCategory(Long id) {
        requireAdmin();
        categoryMapper.deleteById(id);
    }

    // ============ 渠道字典管理 ============

    public List<Channel> listChannels() {
        requireAdmin();
        return channelMapper.selectList(new LambdaQueryWrapper<Channel>().orderByAsc(Channel::getSort));
    }

    public Channel createChannel(Channel channel) {
        requireAdmin();
        channel.setId(null);
        channelMapper.insert(channel);
        return channel;
    }

    public Channel updateChannel(Long id, Channel channel) {
        requireAdmin();
        if (channelMapper.selectById(id) == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        channel.setId(id);
        channelMapper.updateById(channel);
        return channel;
    }

    public void deleteChannel(Long id) {
        requireAdmin();
        channelMapper.deleteById(id);
    }

    // ============ 成本类型字典管理 ============

    public List<CostType> listCostTypes() {
        requireAdmin();
        return costTypeMapper.selectList(new LambdaQueryWrapper<CostType>().orderByAsc(CostType::getSort));
    }

    public CostType createCostType(CostType costType) {
        requireAdmin();
        costType.setId(null);
        costTypeMapper.insert(costType);
        return costType;
    }

    public CostType updateCostType(Long id, CostType costType) {
        requireAdmin();
        if (costTypeMapper.selectById(id) == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        costType.setId(id);
        costTypeMapper.updateById(costType);
        return costType;
    }

    public void deleteCostType(Long id) {
        requireAdmin();
        costTypeMapper.deleteById(id);
    }

    // ============ 通用字典管理 ============

    public List<SysDict> listDicts(String dictType) {
        requireAdmin();
        return dictMapper.selectList(new LambdaQueryWrapper<SysDict>()
                .eq(dictType != null && !dictType.isBlank(), SysDict::getDictType, dictType)
                .orderByAsc(SysDict::getSort));
    }

    /**
     * 字典类型列表（供前端下拉）
     */
    public List<DictTypeVO> listDictTypes() {
        requireAdmin();
        return Arrays.stream(DictType.values())
                .map(t -> new DictTypeVO(t.getCode(), t.getLabel()))
                .collect(Collectors.toList());
    }

    public SysDict createDict(SysDict dict) {
        requireAdmin();
        if (!DictType.isValid(dict.getDictType())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "不支持的字典类型");
        }
        dict.setId(null);
        dictMapper.insert(dict);
        return dict;
    }

    public SysDict updateDict(Long id, SysDict dict) {
        requireAdmin();
        if (dictMapper.selectById(id) == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        if (!DictType.isValid(dict.getDictType())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "不支持的字典类型");
        }
        dict.setId(id);
        dictMapper.updateById(dict);
        return dict;
    }

    public void deleteDict(Long id) {
        requireAdmin();
        dictMapper.deleteById(id);
    }

    // ============ 全平台跳转统计 ============

    public AdminClickStatsVO clickStats() {
        requireAdmin();
        List<ClickLog> list = clickLogMapper.selectList(null);
        Map<Long, Long> grouped = list.stream()
                .collect(Collectors.groupingBy(ClickLog::getShopId, Collectors.counting()));
        List<ClickStatItemVO> byShop = grouped.entrySet().stream()
                .map(e -> new ClickStatItemVO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
        AdminClickStatsVO vo = new AdminClickStatsVO();
        vo.setTotal(list.size());
        vo.setByShop(byShop);
        return vo;
    }
}
