package com.farm.controller;

import com.farm.common.Result;
import com.farm.service.FileStorageService;
import com.farm.vo.FileUploadVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传接口（需登录；当前用于产品图片）
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    /**
     * 上传文件到 MinIO
     *
     * @param file 文件（jpg/jpeg/png/webp/gif，≤5MB）
     * @return 可访问 URL
     */
    @PostMapping("/upload")
    public Result<FileUploadVO> upload(@RequestParam("file") MultipartFile file) {
        return Result.ok(fileStorageService.upload(file));
    }
}
