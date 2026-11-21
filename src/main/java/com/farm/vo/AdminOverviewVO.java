package com.farm.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 平台整体统计响应（替换原 Map）
 */
@Data
public class AdminOverviewVO {

    private Long userCount;
    private Long shopCount;
    private Long pendingShopCount;
    private Long productCount;
    private Long salesCount;
    private BigDecimal totalSalesAmount;
}
