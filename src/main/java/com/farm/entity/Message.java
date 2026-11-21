package com.farm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 留言咨询表实体
 */
@Data
@TableName("message")
public class Message {

    /** 记录ID（主键，自增） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 商家（店铺）ID */
    private Long shopId;

    /** 留言用户ID */
    private Long userId;

    /** 留言内容 */
    private String content;

    /** 商家回复内容 */
    private String reply;

    /** 状态：0-待回复 1-已回复 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 回复时间 */
    private LocalDateTime replyTime;
}
