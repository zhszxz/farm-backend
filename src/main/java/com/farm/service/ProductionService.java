package com.farm.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.farm.common.BusinessException;
import com.farm.common.ErrorCode;
import com.farm.common.PageResult;
import com.farm.entity.ProductionRecord;
import com.farm.mapper.ProductionRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 产量记录服务（商家工作台）
 */
@Service
@RequiredArgsConstructor
public class ProductionService {

    private final ProductionRecordMapper productionMapper;
    private final ShopService shopService;

    /**
     * 当前店铺产量记录列表（支持年份/品种筛选，分页）
     *
     * @param year       年份
     * @param categoryId 品种ID
     * @param pageNum    页码（1 起）
     * @param pageSize   每页条数
     * @return 分页结果
     */
    public PageResult<ProductionRecord> list(Integer year, Long categoryId, long pageNum, long pageSize) {
        Long shopId = shopService.getCurrentShop().getId();
        Page<ProductionRecord> page = new Page<>(pageNum, pageSize);
        productionMapper.selectPage(page, new LambdaQueryWrapper<ProductionRecord>()
                .eq(ProductionRecord::getShopId, shopId)
                .eq(year != null, ProductionRecord::getYear, year)
                .eq(categoryId != null, ProductionRecord::getCategoryId, categoryId)
                .orderByDesc(ProductionRecord::getYear)
                .orderByDesc(ProductionRecord::getCreateTime));
        return PageResult.of(page);
    }

    /**
     * 新增产量记录
     */
    public ProductionRecord create(ProductionRecord record) {
        Long shopId = shopService.getCurrentShop().getId();
        validate(record);
        record.setId(null);
        record.setShopId(shopId);
        productionMapper.insert(record);
        return record;
    }

    /**
     * 编辑产量记录（校验归属）
     */
    public ProductionRecord update(Long id, ProductionRecord req) {
        ProductionRecord record = requireOwn(id);
        validate(req);
        record.setCategoryId(req.getCategoryId());
        record.setYear(req.getYear());
        record.setArea(req.getArea());
        record.setQuantity(req.getQuantity());
        record.setUnit(req.getUnit());
        record.setRemark(req.getRemark());
        productionMapper.updateById(record);
        return record;
    }

    /**
     * 删除产量记录（校验归属，逻辑删除）
     */
    public void delete(Long id) {
        requireOwn(id);
        productionMapper.deleteById(id);
    }

    private void validate(ProductionRecord record) {
        if (record.getYear() == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "年份不能为空");
        }
        if (record.getCategoryId() == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "品种不能为空");
        }
        if (record.getQuantity() == null || record.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "产量必须大于 0");
        }
    }

    private ProductionRecord requireOwn(Long id) {
        ProductionRecord record = productionMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        Long shopId = shopService.getCurrentShop().getId();
        if (!shopId.equals(record.getShopId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return record;
    }
}
