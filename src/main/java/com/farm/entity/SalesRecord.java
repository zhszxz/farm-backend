package com.farm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 销售记录表实体（核心：某年某品种某渠道卖了多少多少钱）
 */
@Data
@TableName("sales_record")
public class SalesRecord {

    /** 记录ID（主键，自增） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属店铺ID */
    private Long shopId;

    /** 品种（小类）ID，关联 category */
    private Long categoryId;

    /** 销售年份（如 2024） */
    private Integer year;

    /** 销售渠道ID，关联 channel */
    private Long channelId;

    /** 销售数量 */
    private BigDecimal quantity;

    /** 计量单位（斤/公斤/箱/吨） */
    private String unit;

    /** 销售额（元） */
    private BigDecimal amount;

    /** 单价（元/单位，可自动计算） */
    private BigDecimal unitPrice;

    /** 销售时间段（如"2024年10月"） */
    private String salePeriod;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 逻辑删除：0-否 1-是 */
    private Integer deleted;
}
