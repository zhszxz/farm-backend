package com.farm.vo;

import lombok.Data;

/**
 * 文件上传响应
 */
@Data
public class FileUploadVO {

    /** 可访问的 HTTP URL */
    private String url;
    /** 对象名（桶内路径） */
    private String objectName;
    /** 原始文件名 */
    private String originalName;
}
