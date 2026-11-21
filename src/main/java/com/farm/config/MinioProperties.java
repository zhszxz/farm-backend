package com.farm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * MinIO 配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {

    private String endpoint = "http://localhost:9000";
    private String accessKey = "minioadmin";
    private String secretKey = "minioadmin";
    private String bucket = "farm";
    private String publicUrl = "http://localhost:9000";
}
