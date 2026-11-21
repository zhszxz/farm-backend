package com.farm.enums;

/**
 * 通用字典类型枚举（对应 sys_dict.dict_type）
 */
public enum DictType {

    /** 计量单位：斤 / 公斤 / 箱 / 吨 */
    UNIT("unit", "计量单位"),
    /** 上市季节：春 / 夏 / 秋 / 冬 / 全年 */
    SEASON("season", "上市季节"),
    /** 品质等级：特级 / 一级 / 二级 … */
    GRADE("grade", "品质等级"),
    /** 包装方式：散装 / 礼盒 / 纸箱 … */
    PACKING("packing", "包装方式"),
    /** 跳转平台：拼多多 / 淘宝 / 抖音 … */
    LINK_PLATFORM("link_platform", "跳转平台");

    private final String code;
    private final String label;

    DictType(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    /**
     * 根据 code 查找，找不到返回 null
     */
    public static DictType fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (DictType t : values()) {
            if (t.code.equals(code)) {
                return t;
            }
        }
        return null;
    }

    /**
     * 是否为受支持的字典类型
     */
    public static boolean isValid(String code) {
        return fromCode(code) != null;
    }
}
