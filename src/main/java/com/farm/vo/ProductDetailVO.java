package com.farm.vo;

import lombok.Data;

/**
 * 产品详情响应（替换原 Map）
 */
@Data
public class ProductDetailVO {

    private Long id;
    private String name;
    private Long categoryId;
    private String categoryName;
    private String bigCategoryName;
    private String unit;
    /** 图片 JSON 字符串（原字段） */
    private String images;
    private String description;
    private String origin;
    private String season;
    private ShopBriefVO shop;
}
