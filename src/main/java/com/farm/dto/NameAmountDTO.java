package com.farm.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 名称-金额通用报表行（用于产品对比、渠道占比、品类占比、排行）
 */
@Data
public class NameAmountDTO {

    /** 维度ID（品种/渠道/大类ID） */
    private Long id;

    /** 维度名称（品种名/渠道名/大类名） */
    private String name;

    /** 金额（元） */
    private BigDecimal amount;
}
