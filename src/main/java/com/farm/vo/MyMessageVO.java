package com.farm.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 我发出的留言聚合 VO（含店铺名）
 */
@Data
public class MyMessageVO {

    private Long id;
    private Long shopId;
    /** 商家店铺名 */
    private String shopName;
    private String content;
    private String reply;
    /** 0-待回复 1-已回复 */
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime replyTime;
}
