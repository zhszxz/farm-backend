package com.farm.dto;

import lombok.Data;

/**
 * 跳转记录请求（用户点击"去拼多多购买"）
 */
@Data
public class ClickRequest {

    /** 店铺ID */
    private Long shopId;

    /** 产品ID（可空） */
    private Long productId;

    /** 链接ID（可空） */
    private Long linkId;

    /** 来源（可选，如 二维码/详情页） */
    private String source;
}
