package com.farm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 销售渠道字典表实体
 */
@Data
@TableName("channel")
public class Channel {

    /** 渠道ID（主键，自增） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 渠道名称（如"电商平台(拼多多)""批发市场"） */
    private String name;

    /** 排序 */
    private Integer sort;

    /** 状态：1-启用 0-停用 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 逻辑删除：0-否 1-是 */
    private Integer deleted;
}
