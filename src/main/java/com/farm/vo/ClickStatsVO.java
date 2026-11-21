package com.farm.vo;

import com.farm.entity.ClickLog;
import lombok.Data;

import java.util.List;

/**
 * 商家跳转统计响应（替换原 Map）
 */
@Data
public class ClickStatsVO {

    private long total;
    private List<ClickStatItemVO> byProduct;
    private List<ClickLog> recent;
}
