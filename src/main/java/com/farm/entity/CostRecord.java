package com.farm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 成本记录表实体（支撑利润核算）
 */
@Data
@TableName("cost_record")
public class CostRecord {

    /** 记录ID（主键，自增） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属店铺ID */
    private Long shopId;

    /** 品种（小类）ID，关联 category */
    private Long categoryId;

    /** 年份（如 2024） */
    private Integer year;

    /** 成本类型ID，关联 cost_type */
    private Long costTypeId;

    /** 金额（元） */
    private BigDecimal amount;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 逻辑删除：0-否 1-是 */
    private Integer deleted;
}
