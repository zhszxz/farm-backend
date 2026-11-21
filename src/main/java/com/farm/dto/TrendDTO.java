package com.farm.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 单品跨年趋势报表行（按年份）
 */
@Data
public class TrendDTO {

    /** 年份 */
    private Integer year;

    /** 产量 */
    private BigDecimal production;

    /** 销量 */
    private BigDecimal salesQty;

    /** 销售额（元） */
    private BigDecimal salesAmount;
}
