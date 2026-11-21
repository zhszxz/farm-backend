package com.farm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 第三方店铺链接表实体（引流到拼多多）
 */
@Data
@TableName("shop_link")
public class ShopLink {

    /** 链接ID（主键，自增） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属店铺ID */
    private Long shopId;

    /** 关联产品ID（空=店铺级链接） */
    private Long productId;

    /** 平台：pdd-拼多多 */
    private String platform;

    /** 链接名称 */
    private String linkName;

    /** 跳转链接地址 */
    private String linkUrl;

    /** 状态：1-启用 0-停用 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 逻辑删除：0-否 1-是 */
    private Integer deleted;
}
