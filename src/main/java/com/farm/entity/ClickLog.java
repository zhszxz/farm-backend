package com.farm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 跳转统计表实体（记录用户点击"去拼多多购买"）
 */
@Data
@TableName("click_log")
public class ClickLog {

    /** 记录ID（主键，自增） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属店铺ID */
    private Long shopId;

    /** 产品ID（可空） */
    private Long productId;

    /** 链接ID（可空） */
    private Long linkId;

    /** 来源（可选，如 二维码/详情页） */
    private String source;

    /** 点击时间 */
    private LocalDateTime clickTime;
}
