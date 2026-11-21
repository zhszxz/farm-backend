package com.farm.service;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.farm.common.BusinessException;
import com.farm.common.ErrorCode;
import com.farm.common.PageResult;
import com.farm.dto.SalesRecordExcel;
import com.farm.entity.Category;
import com.farm.entity.Channel;
import com.farm.entity.SalesRecord;
import com.farm.mapper.CategoryMapper;
import com.farm.mapper.ChannelMapper;
import com.farm.mapper.SalesRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 销售记录服务（商家工作台核心：销售录入、Excel 导入）
 */
@Service
@RequiredArgsConstructor
public class SalesService {

    private final SalesRecordMapper salesMapper;
    private final CategoryMapper categoryMapper;
    private final ChannelMapper channelMapper;
    private final ShopService shopService;

    /**
     * 当前店铺销售记录列表（支持年份/品种/渠道筛选）
     *
     * @param year       年份
     * @param categoryId 品种ID
     * @param channelId  渠道ID
     * @return 销售记录列表
     */
    public PageResult<SalesRecord> list(Integer year, Long categoryId, Long channelId, long pageNum, long pageSize) {
        Long shopId = shopService.getCurrentShop().getId();
        Page<SalesRecord> page = new Page<>(pageNum, pageSize);
        salesMapper.selectPage(page, new LambdaQueryWrapper<SalesRecord>()
                .eq(SalesRecord::getShopId, shopId)
                .eq(year != null, SalesRecord::getYear, year)
                .eq(categoryId != null, SalesRecord::getCategoryId, categoryId)
                .eq(channelId != null, SalesRecord::getChannelId, channelId)
                .orderByDesc(SalesRecord::getYear)
                .orderByDesc(SalesRecord::getCreateTime));
        return PageResult.of(page);
    }

    /**
     * 新增销售记录
     *
     * @param record 销售记录
     * @return 创建的记录
     */
    public SalesRecord create(SalesRecord record) {
        Long shopId = shopService.getCurrentShop().getId();
        validate(record);
        record.setId(null);
        record.setShopId(shopId);
        fillUnitPrice(record);
        salesMapper.insert(record);
        return record;
    }

    /**
     * 编辑销售记录（校验归属）
     *
     * @param id  记录ID
     * @param req 销售记录
     * @return 更新后的记录
     */
    public SalesRecord update(Long id, SalesRecord req) {
        SalesRecord record = requireOwn(id);
        validate(req);
        record.setCategoryId(req.getCategoryId());
        record.setYear(req.getYear());
        record.setChannelId(req.getChannelId());
        record.setQuantity(req.getQuantity());
        record.setUnit(req.getUnit());
        record.setAmount(req.getAmount());
        record.setUnitPrice(req.getUnitPrice());
        record.setSalePeriod(req.getSalePeriod());
        record.setRemark(req.getRemark());
        fillUnitPrice(record);
        salesMapper.updateById(record);
        return record;
    }

    /**
     * 删除销售记录（校验归属，逻辑删除）
     *
     * @param id 记录ID
     */
    public void delete(Long id) {
        requireOwn(id);
        salesMapper.deleteById(id);
    }

    /**
     * 从 Excel 批量导入销售记录（品种/渠道按名称解析，逐行校验，返回成功条数）
     *
     * @param file Excel 文件（.xlsx）
     * @return 成功导入条数
     */
    public int importExcel(MultipartFile file) {
        Long shopId = shopService.getCurrentShop().getId();
        List<SalesRecordExcel> rows;
        try {
            rows = EasyExcel.read(file.getInputStream())
                    .head(SalesRecordExcel.class)
                    .sheet()
                    .doReadSync();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "Excel 解析失败：" + e.getMessage());
        }
        int success = 0;
        for (SalesRecordExcel row : rows) {
            try {
                SalesRecord record = new SalesRecord();
                record.setShopId(shopId);
                record.setYear(row.getYear());
                record.setCategoryId(resolveCategory(row.getCategoryName()));
                record.setChannelId(resolveChannel(row.getChannelName()));
                record.setQuantity(row.getQuantity());
                record.setUnit(row.getUnit());
                record.setAmount(row.getAmount());
                record.setUnitPrice(row.getUnitPrice());
                record.setRemark(row.getRemark());
                validate(record);
                fillUnitPrice(record);
                salesMapper.insert(record);
                success++;
            } catch (BusinessException ignored) {
                // 单行失败跳过，不影响其它行
            }
        }
        return success;
    }

    /**
     * 按名称解析品种（小类）
     */
    private Long resolveCategory(String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "品种名称不能为空");
        }
        Category category = categoryMapper.selectOne(new LambdaQueryWrapper<Category>()
                .eq(Category::getName, name)
                .eq(Category::getLevel, 2)
                .last("LIMIT 1"));
        if (category == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND.getCode(), "品种不存在：" + name);
        }
        return category.getId();
    }

    /**
     * 按名称解析渠道
     */
    private Long resolveChannel(String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "渠道名称不能为空");
        }
        Channel channel = channelMapper.selectOne(new LambdaQueryWrapper<Channel>()
                .eq(Channel::getName, name)
                .last("LIMIT 1"));
        if (channel == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND.getCode(), "渠道不存在：" + name);
        }
        return channel.getId();
    }

    /**
     * 字段校验
     */
    private void validate(SalesRecord record) {
        if (record.getYear() == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "年份不能为空");
        }
        if (record.getCategoryId() == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "品种不能为空");
        }
        if (record.getChannelId() == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "渠道不能为空");
        }
        if (record.getQuantity() == null || record.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "数量必须大于 0");
        }
        if (record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "销售额不能为负");
        }
    }

    /**
     * 单价未填时按 销售额/数量 自动计算
     */
    private void fillUnitPrice(SalesRecord record) {
        if (record.getUnitPrice() == null
                && record.getQuantity() != null
                && record.getQuantity().compareTo(BigDecimal.ZERO) != 0
                && record.getAmount() != null) {
            record.setUnitPrice(record.getAmount().divide(record.getQuantity(), 2, RoundingMode.HALF_UP));
        }
    }

    /**
     * 校验记录属于当前店铺
     */
    private SalesRecord requireOwn(Long id) {
        SalesRecord record = salesMapper.selectById(id);
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
