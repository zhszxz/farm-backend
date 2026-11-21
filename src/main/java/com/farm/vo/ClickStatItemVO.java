package com.farm.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 跳转统计单项（按产品 / 按店铺）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClickStatItemVO {

    /** 维度ID（产品ID 或 店铺ID） */
    private Long id;
    /** 点击数 */
    private Long count;
}
