package com.farm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 产品分类表实体（大类/小类两级，自关联）
 */
@Data
@TableName("category")
public class Category {

    /** 分类ID（主键，自增） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 分类名称（如"苹果""红富士"） */
    private String name;

    /** 父级ID：0-大类，非0-所属大类ID */
    private Long parentId;

    /** 层级：1-大类 2-小类 */
    private Integer level;

    /** 排序（升序） */
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
