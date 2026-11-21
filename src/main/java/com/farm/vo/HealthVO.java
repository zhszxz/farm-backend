package com.farm.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 健康检查响应（替换原 Map）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthVO {

    private String status;
    private String db;
}
