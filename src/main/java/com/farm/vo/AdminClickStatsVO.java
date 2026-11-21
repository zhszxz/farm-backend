package com.farm.vo;

import lombok.Data;

import java.util.List;

/**
 * 全平台跳转统计响应（替换原 Map）
 */
@Data
public class AdminClickStatsVO {

    private long total;
    private List<ClickStatItemVO> byShop;
}
