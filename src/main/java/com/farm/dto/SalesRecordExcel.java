package com.farm.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 销售记录 Excel 导入模板（表头与字段一一对应）
 */
@Data
public class SalesRecordExcel {

    /** 年份（如 2024） */
    @ExcelProperty("年份")
    private Integer year;

    /** 品种（小类）名称（如 红富士） */
    @ExcelProperty("品种")
    private String categoryName;

    /** 渠道名称（如 电商平台(拼多多)） */
    @ExcelProperty("渠道")
    private String channelName;

    /** 销售数量 */
    @ExcelProperty("数量")
    private BigDecimal quantity;

    /** 计量单位（斤/公斤/箱/吨） */
    @ExcelProperty("单位")
    private String unit;

    /** 销售额（元） */
    @ExcelProperty("销售额(元)")
    private BigDecimal amount;

    /** 单价（元，可留空自动计算） */
    @ExcelProperty("单价(元)")
    private BigDecimal unitPrice;

    /** 备注 */
    @ExcelProperty("备注")
    private String remark;
}
