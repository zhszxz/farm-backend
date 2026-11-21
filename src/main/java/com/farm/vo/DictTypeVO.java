package com.farm.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 字典类型项（供前端下拉使用）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DictTypeVO {

    /** 类型编码，如 unit */
    private String code;

    /** 类型名称，如 计量单位 */
    private String label;
}
