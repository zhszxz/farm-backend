package com.farm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 店铺表实体（商家入驻后拥有）
 */
@Data
@TableName("shop")
public class Shop {

    /** 店铺ID（主键，自增） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属用户ID（一个用户最多一个店铺） */
    private Long userId;

    /** 店铺名称 */
    private String shopName;

    /** 所在地区（省市区县乡镇） */
    private String region;

    /** 详细地址 */
    private String address;

    /** 联系人 */
    private String contactName;

    /** 联系电话 */
    private String contactPhone;

    /** 微信号 */
    private String wechat;

    /** 店铺简介 */
    private String description;

    /** 资质证明图片 URL（可选） */
    private String licenseImage;

    /** 审核状态：0-待审核 1-通过 2-驳回 */
    private Integer status;

    /** 驳回原因 */
    private String rejectReason;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 逻辑删除：0-否 1-是 */
    private Integer deleted;
}
