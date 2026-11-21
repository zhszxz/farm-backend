package com.farm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 商家入驻申请请求
 */
@Data
public class ShopApplyRequest {

    @NotBlank(message = "店铺名称不能为空")
    @Size(min = 2, max = 30, message = "店铺名称需 2-30 个字")
    private String shopName;

    @NotBlank(message = "所在地区不能为空")
    private String region;

    @NotBlank(message = "详细地址不能为空")
    private String address;

    @NotBlank(message = "联系人不能为空")
    private String contactName;

    @NotBlank(message = "联系电话不能为空")
    private String contactPhone;

    /** 微信号（可选） */
    private String wechat;

    @Size(max = 200, message = "店铺简介不超过 200 字")
    private String description;

    /** 资质证明图片 URL（可选） */
    private String licenseImage;
}
