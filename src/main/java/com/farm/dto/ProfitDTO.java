package com.farm.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 利润核算报表行（按年份）
 */
@Data
public class ProfitDTO {

    /** 年份 */
    @ExcelProperty("年份")
    private Integer year;

    /** 销售额（元） */
    @ExcelProperty("销售额(元)")
    private BigDecimal salesAmount;

    /** 成本（元） */
    @ExcelProperty("成本(元)")
    private BigDecimal costAmount;

    /** 利润（元）= 销售额 - 成本 */
    @ExcelProperty("利润(元)")
    private BigDecimal profit;

    /** 利润率（%）= 利润 / 销售额 */
    @ExcelProperty("利润率(%)")
    private BigDecimal profitRate;
}
