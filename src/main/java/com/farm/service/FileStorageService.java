package com.farm.service;

import com.farm.common.BusinessException;
import com.farm.common.ErrorCode;
import com.farm.config.MinioProperties;
import com.farm.vo.FileUploadVO;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.SetBucketPolicyArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

/**
 * 文件存储服务（MinIO）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {

    private static final long MAX_SIZE = 5 * 1024 * 1024L;
    private static final String[] ALLOWED = {"jpg", "jpeg", "png", "webp", "gif"};

    private final MinioClient minioClient;
    private final MinioProperties properties;

    /**
     * 上传文件到 MinIO，返回可访问 URL
     *
     * @param file 上传文件
     * @return 上传结果
     */
    public FileUploadVO upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "文件不能为空");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "文件不能超过 5MB");
        }
        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf('.') + 1).toLowerCase();
        }
        if (!isAllowed(ext)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "仅支持 jpg/jpeg/png/webp/gif");
        }
        String objectName = "products/" + UUID.randomUUID().toString().replace("-", "") + "." + ext;
        try {
            ensureBucket();
            try (InputStream in = file.getInputStream()) {
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(properties.getBucket())
                        .object(objectName)
                        .stream(in, file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build());
            }
        } catch (Exception e) {
            log.error("MinIO 上传失败", e);
            throw new BusinessException(ErrorCode.SERVER_ERROR.getCode(), "文件上传失败：" + e.getMessage());
        }
        FileUploadVO vo = new FileUploadVO();
        vo.setObjectName(objectName);
        vo.setOriginalName(original);
        vo.setUrl(properties.getPublicUrl() + "/" + properties.getBucket() + "/" + objectName);
        return vo;
    }

    private boolean isAllowed(String ext) {
        for (String a : ALLOWED) {
            if (a.equals(ext)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 确保桶存在，并设置只读公开策略（图片可直接通过 URL 访问）
     */
    private void ensureBucket() throws Exception {
        String bucket = properties.getBucket();
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
        // 无论桶是新建还是已存在，都幂等设置公开只读策略，
        // 否则桶被手工创建时不会带策略，上传后的图片 URL 会 403。
        String policy = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\","
                + "\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetObject\"],"
                + "\"Resource\":[\"arn:aws:s3:::" + bucket + "/*\"]}]}";
        minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
                .bucket(bucket).config(policy).build());
    }
}
