package com.farm.vo;

import lombok.Data;

/**
 * 商家简要信息（嵌在产品详情中）
 */
@Data
public class ShopBriefVO {

    private Long id;
    private String shopName;
    private String wechat;
    private String contactPhone;
    private String linkUrl;
}
