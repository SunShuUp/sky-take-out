package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;


@RestController
@RequestMapping("/admin/common")
@Api(tags = "公共类相关接口")
@Slf4j
public class CommonController {
    @PostMapping("/upload")
    @ApiOperation("上传接口")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        // 1. 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        // 2. 生成新文件名（避免重名）
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString() + extension;

        // 3. 确定存储目录（使用用户目录下的 upload 文件夹）
        String userHome = System.getProperty("user.home");
        String uploadDir = userHome + "/upload/";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 4. 存储文件
        File dest = new File(uploadDir + newFileName);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error("上传失败：" + e.getMessage());
        }

        // 5. 返回可访问的路径（通常是静态资源映射或绝对路径）
        // 方式A：返回绝对路径（不推荐用于前端展示）
        String filePath = dest.getAbsolutePath();

        // 方式B：返回相对路径或虚拟路径（配合静态资源配置）
        // 比如 /upload/xxx.jpg，前端可通过域名+路径访问
        // String filePath = "/upload/" + newFileName;

        return Result.success(filePath);
    }
}
