package com.farm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通用字典表实体（单位、上市季节等）
 */
@Data
@TableName("sys_dict")
public class SysDict {

    /** 字典ID（主键，自增） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 字典类型：unit-单位 season-上市季节 */
    private String dictType;

    /** 字典值（如 jin、spring） */
    private String dictValue;

    /** 显示名称（如 斤、春季） */
    private String dictLabel;

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
