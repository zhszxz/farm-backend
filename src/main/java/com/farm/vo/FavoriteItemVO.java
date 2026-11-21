package com.farm.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收藏项聚合 VO（解决"只有 ID"问题）
 */
@Data
public class FavoriteItemVO {

    /** 收藏记录ID */
    private Long id;
    /** 目标类型：1-产品 2-店铺 */
    private Integer targetType;
    /** 目标ID */
    private Long targetId;
    /** 产品名 或 店铺名 */
    private String name;
    /** 产品首图（店铺收藏为空） */
    private String coverImage;
    /** 产品所属店铺名 / 店铺名 */
    private String shopName;
    /** 目标状态：产品 0草稿/1上架/2下架；店铺 0待审/1通过/2驳回；-1 已失效 */
    private Integer targetStatus;
    /** 收藏时间 */
    private LocalDateTime createTime;
}
