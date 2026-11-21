package com.farm.vo;

import lombok.Data;

/**
 * 商家主页响应（替换原 Map）
 */
@Data
public class ShopHomeVO {

    private Long id;
    private String shopName;
    private String region;
    private String address;
    private String contactName;
    private String contactPhone;
    private String wechat;
    private String description;
    private String linkUrl;
}
