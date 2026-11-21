package com.farm.dto;

import lombok.Data;

/**
 * 商家审核请求（替换原 Map）
 */
@Data
public class ShopAuditRequest {

    /** true 通过 / false 驳回 */
    private Boolean pass;
    /** 驳回原因（驳回时必填） */
    private String reason;
}
