package com.farm.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.farm.common.BusinessException;
import com.farm.common.ErrorCode;
import com.farm.common.PageResult;
import com.farm.entity.CostRecord;
import com.farm.mapper.CostRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 成本记录服务（商家工作台）
 */
@Service
@RequiredArgsConstructor
public class CostService {

    private final CostRecordMapper costMapper;
    private final ShopService shopService;

    /**
     * 当前店铺成本记录列表（支持年份/品种/成本类型筛选，分页）
     *
     * @param year       年份
     * @param categoryId 品种ID
     * @param costTypeId 成本类型ID
     * @param pageNum    页码（1 起）
     * @param pageSize   每页条数
     * @return 分页结果
     */
    public PageResult<CostRecord> list(Integer year, Long categoryId, Long costTypeId, long pageNum, long pageSize) {
        Long shopId = shopService.getCurrentShop().getId();
        Page<CostRecord> page = new Page<>(pageNum, pageSize);
        costMapper.selectPage(page, new LambdaQueryWrapper<CostRecord>()
                .eq(CostRecord::getShopId, shopId)
                .eq(year != null, CostRecord::getYear, year)
                .eq(categoryId != null, CostRecord::getCategoryId, categoryId)
                .eq(costTypeId != null, CostRecord::getCostTypeId, costTypeId)
                .orderByDesc(CostRecord::getYear)
                .orderByDesc(CostRecord::getCreateTime));
        return PageResult.of(page);
    }

    /**
     * 新增成本记录
     */
    public CostRecord create(CostRecord record) {
        Long shopId = shopService.getCurrentShop().getId();
        validate(record);
        record.setId(null);
        record.setShopId(shopId);
        costMapper.insert(record);
        return record;
    }

    /**
     * 编辑成本记录（校验归属）
     */
    public CostRecord update(Long id, CostRecord req) {
        CostRecord record = requireOwn(id);
        validate(req);
        record.setCategoryId(req.getCategoryId());
        record.setYear(req.getYear());
        record.setCostTypeId(req.getCostTypeId());
        record.setAmount(req.getAmount());
        record.setRemark(req.getRemark());
        costMapper.updateById(record);
        return record;
    }

    /**
     * 删除成本记录（校验归属，逻辑删除）
     */
    public void delete(Long id) {
        requireOwn(id);
        costMapper.deleteById(id);
    }

    private void validate(CostRecord record) {
        if (record.getYear() == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "年份不能为空");
        }
        if (record.getCategoryId() == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "品种不能为空");
        }
        if (record.getCostTypeId() == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "成本类型不能为空");
        }
        if (record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "金额不能为负");
        }
    }

    private CostRecord requireOwn(Long id) {
        CostRecord record = costMapper.selectById(id);
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
