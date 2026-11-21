package com.farm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 产量记录表实体（支撑产量趋势报表）
 */
@Data
@TableName("production_record")
public class ProductionRecord {

    /** 记录ID（主键，自增） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属店铺ID */
    private Long shopId;

    /** 品种（小类）ID，关联 category */
    private Long categoryId;

    /** 年份（如 2024） */
    private Integer year;

    /** 种植面积（亩） */
    private BigDecimal area;

    /** 产量 */
    private BigDecimal quantity;

    /** 计量单位（斤/公斤/箱/吨） */
    private String unit;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 逻辑删除：0-否 1-是 */
    private Integer deleted;
}
