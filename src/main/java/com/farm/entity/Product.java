package com.farm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 产品表实体（面向访客的展示条目）
 */
@Data
@TableName("product")
public class Product {

    /** 产品ID（主键，自增） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属店铺ID */
    private Long shopId;

    /** 品种（小类）ID，关联 category */
    private Long categoryId;

    /** 产品名称（展示用，如"红富士苹果5斤装"） */
    private String name;

    /** 计量单位（斤/公斤/箱/吨） */
    private String unit;

    /** 图片 URL 列表（JSON 字符串，如 ["https://...a.jpg"]） */
    private String images;

    /** 产品介绍 */
    private String description;

    /** 产地 */
    private String origin;

    /** 上市季节 */
    private String season;

    /** 状态：0-草稿 1-已上架 2-已下架 */
    private Integer status;

    /** 是否推荐：0-否 1-是 */
    private Integer isRecommend;

    /** 价格（元，商城预留字段） */
    private BigDecimal price;

    /** 库存（商城预留字段） */
    private Integer stock;

    /** 规格/SKU（商城预留字段） */
    private String sku;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 逻辑删除：0-否 1-是 */
    private Integer deleted;
}
