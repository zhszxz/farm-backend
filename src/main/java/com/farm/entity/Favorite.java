package com.farm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收藏关注表实体（物理删除实现 toggle）
 */
@Data
@TableName("favorite")
public class Favorite {

    /** 记录ID（主键，自增） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 目标类型：1-产品 2-店铺 */
    private Integer targetType;

    /** 目标ID（产品ID或店铺ID） */
    private Long targetId;

    /** 收藏时间 */
    private LocalDateTime createTime;
}
