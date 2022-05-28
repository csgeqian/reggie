package com.itheima.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传与下载
 *
 * @author jektong
 * @date 2022年05月13日 0:43
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件上传
     *
     * @param file
     * @return R
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        log.info(file.toString());
        // 原始文件名
        String originalFilename = file.getOriginalFilename();
        // 截取出文件
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 使用UUID重新生成文件名防止文件覆盖
        String fileName = UUID.randomUUID().toString() + suffix;
        // 创建一个目录对象
        File dir = new File(this.basePath);
        if (!dir.exists()) {
            // 目录不存在，需要创建。
            dir.mkdir();
        }
        try {
            // 将文件输出到指定的位置
            file.transferTo(new File(this.basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }


    /**
     * 文件下载
     *
     * @param name     文件名
     * @param response 文件数据
     */
    @GetMapping("/download")
    public void downLoad(String name, HttpServletResponse response) {
        try {
            // 读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(this.basePath + name));
            // 输出流，用于写入浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
            // 关闭资源
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
