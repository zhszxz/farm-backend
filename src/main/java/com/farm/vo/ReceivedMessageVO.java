package com.farm.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商家收到的留言聚合 VO（含留言人昵称）
 */
@Data
public class ReceivedMessageVO {

    private Long id;
    private Long userId;
    /** 留言人昵称 */
    private String userNickname;
    private String content;
    private String reply;
    /** 0-待回复 1-已回复 */
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime replyTime;
}
