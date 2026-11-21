package com.farm.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.farm.common.BusinessException;
import com.farm.common.ErrorCode;
import com.farm.common.PageResult;
import com.farm.dto.ShopApplyRequest;
import com.farm.entity.Shop;
import com.farm.entity.ShopLink;
import com.farm.mapper.ShopLinkMapper;
import com.farm.mapper.ShopMapper;
import com.farm.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 店铺服务：入驻申请、店铺信息、拼多多链接
 */
@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopMapper shopMapper;
    private final ShopLinkMapper shopLinkMapper;

    /**
     * 获取当前登录商家的店铺（需审核通过，否则抛异常）
     *
     * @return 当前店铺
     */
    public Shop getCurrentShop() {
        Long userId = UserContext.userId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        Shop shop = shopMapper.selectOne(new LambdaQueryWrapper<Shop>().eq(Shop::getUserId, userId));
        if (shop == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND.getCode(), "尚未申请商家入驻");
        }
        if (shop.getStatus() == null || shop.getStatus() != 1) {
            throw new BusinessException(ErrorCode.FORBIDDEN.getCode(), "店铺未通过审核");
        }
        return shop;
    }

    /**
     * 申请成为商家（创建待审核店铺）
     *
     * @param shop 店铺申请信息
     * @return 创建的店铺
     */
    public Shop apply(ShopApplyRequest req) {
        Long userId = UserContext.userId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        Shop existing = shopMapper.selectOne(
                new LambdaQueryWrapper<Shop>().eq(Shop::getUserId, userId));
        if (existing != null) {
            // 被驳回的申请允许重新提交：更新资料并重置为待审核
            if (existing.getStatus() != null && existing.getStatus() == 2) {
                shopMapper.update(null, new LambdaUpdateWrapper<Shop>()
                        .eq(Shop::getId, existing.getId())
                        .set(Shop::getShopName, req.getShopName())
                        .set(Shop::getRegion, req.getRegion())
                        .set(Shop::getAddress, req.getAddress())
                        .set(Shop::getContactName, req.getContactName())
                        .set(Shop::getContactPhone, req.getContactPhone())
                        .set(Shop::getWechat, req.getWechat())
                        .set(Shop::getDescription, req.getDescription())
                        .set(Shop::getLicenseImage, req.getLicenseImage())
                        .set(Shop::getStatus, 0)
                        .set(Shop::getRejectReason, null));
                existing.setShopName(req.getShopName());
                existing.setRegion(req.getRegion());
                existing.setAddress(req.getAddress());
                existing.setContactName(req.getContactName());
                existing.setContactPhone(req.getContactPhone());
                existing.setWechat(req.getWechat());
                existing.setDescription(req.getDescription());
                existing.setLicenseImage(req.getLicenseImage());
                existing.setStatus(0);
                existing.setRejectReason(null);
                return existing;
            }
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "已提交过入驻申请");
        }
        Shop shop = new Shop();
        shop.setUserId(userId);
        shop.setShopName(req.getShopName());
        shop.setRegion(req.getRegion());
        shop.setAddress(req.getAddress());
        shop.setContactName(req.getContactName());
        shop.setContactPhone(req.getContactPhone());
        shop.setWechat(req.getWechat());
        shop.setDescription(req.getDescription());
        shop.setLicenseImage(req.getLicenseImage());
        shop.setStatus(0);
        shopMapper.insert(shop);
        return shop;
    }

    /**
     * 查询我的入驻状态（可能为 null）
     *
     * @return 店铺或 null
     */
    public Shop myApply() {
        Long userId = UserContext.userId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return shopMapper.selectOne(new LambdaQueryWrapper<Shop>().eq(Shop::getUserId, userId));
    }

    /**
     * 更新店铺信息（仅可改可编辑字段）
     *
     * @param req 店铺信息
     * @return 更新后的店铺
     */
    public Shop updateShop(Shop req) {
        Shop shop = getCurrentShop();
        shop.setShopName(req.getShopName());
        shop.setRegion(req.getRegion());
        shop.setAddress(req.getAddress());
        shop.setContactName(req.getContactName());
        shop.setContactPhone(req.getContactPhone());
        shop.setWechat(req.getWechat());
        shop.setDescription(req.getDescription());
        shop.setLicenseImage(req.getLicenseImage());
        shopMapper.updateById(shop);
        return shop;
    }

    /**
     * 当前店铺的拼多多链接列表
     *
     * @return 链接列表
     */
    public PageResult<ShopLink> listLinks(long pageNum, long pageSize) {
        Long shopId = getCurrentShop().getId();
        Page<ShopLink> page = new Page<>(pageNum, pageSize);
        shopLinkMapper.selectPage(page, new LambdaQueryWrapper<ShopLink>()
                .eq(ShopLink::getShopId, shopId)
                .orderByDesc(ShopLink::getCreateTime));
        return PageResult.of(page);
    }

    /**
     * 新增拼多多链接
     *
     * @param link 链接信息
     * @return 创建的链接
     */
    public ShopLink addLink(ShopLink link) {
        Long shopId = getCurrentShop().getId();
        link.setId(null);
        link.setShopId(shopId);
        if (link.getPlatform() == null || link.getPlatform().isBlank()) {
            link.setPlatform("pdd");
        }
        if (link.getStatus() == null) {
            link.setStatus(1);
        }
        shopLinkMapper.insert(link);
        return link;
    }

    /**
     * 更新拼多多链接（校验归属）
     *
     * @param id  链接ID
     * @param req 链接信息
     * @return 更新后的链接
     */
    public ShopLink updateLink(Long id, ShopLink req) {
        ShopLink link = requireOwnLink(id);
        link.setLinkName(req.getLinkName());
        link.setLinkUrl(req.getLinkUrl());
        link.setProductId(req.getProductId());
        link.setPlatform(req.getPlatform());
        link.setStatus(req.getStatus());
        shopLinkMapper.updateById(link);
        return link;
    }

    /**
     * 删除拼多多链接（校验归属）
     *
     * @param id 链接ID
     */
    public void deleteLink(Long id) {
        requireOwnLink(id);
        shopLinkMapper.deleteById(id);
    }

    /**
     * 校验链接属于当前店铺
     *
     * @param id 链接ID
     * @return 链接
     */
    private ShopLink requireOwnLink(Long id) {
        ShopLink link = shopLinkMapper.selectById(id);
        if (link == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        Long shopId = getCurrentShop().getId();
        if (!shopId.equals(link.getShopId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return link;
    }
}
