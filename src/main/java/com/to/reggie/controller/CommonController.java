package com.to.reggie.controller;

import com.to.reggie.common.R;
import com.to.reggie.controller.ex.*;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 进行文件上传和下载
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController extends BaseController{

    //映射路径
    @Value("${fileIO.path}")
    private String basePath;

    //图片大小
    private static int IMG_SIZE = 1024*1024*10;

    /** 设置上传文件类型 */
    public static final List<String> IMG_TYPE = new ArrayList<>();
    //静态变量初始化块
    static {
        IMG_TYPE.add("image/jpeg");
        IMG_TYPE.add("image/png");
        IMG_TYPE.add("image/bmp");
        IMG_TYPE.add("image/gif");
    }

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {//变量名要和前端input标签里name属性值一致
        //接收后，变成临时文件存在C盘用户目录下，需要地址映射转存
        //log.info("文件"+file.toString());

        if (file.isEmpty()) {
            throw new FileEmptyException("文件不存在异常");
        }

        if (file.getSize() > IMG_SIZE) {
            throw new FileSizeException("文件大小不符合");
        }

        String contentType = file.getContentType();
        if (!IMG_TYPE.contains(contentType)) {
            throw new FileTypeException("文件类型不符合");
        }

        //使用UUID防止文件名重复
        String uuid = UUID.randomUUID().toString();
        //文件类型后缀
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //组合新文件名
        String newFileName = uuid + suffix;

        //如果目录不存在就新建目录
        File dir = new File(basePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        //按映射地址转存
        try {
            file.transferTo(new File(basePath + newFileName));
        } catch (IOException e) {
            throw new FileTransferException("文件传输异常");
        } catch (FileStateException e) {
            throw new FileStateException("文件状态异常");
        }

        //将新文件名传给前端
        return R.success(newFileName);
    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public R<String> download(String name, HttpServletResponse response) {
        try {
            //输入流，读取文件
            FileInputStream fileInputStream = new FileInputStream(basePath + name);
            //输出流，写到浏览器页面
            ServletOutputStream outputStream = response.getOutputStream();

//            //设置类型
//            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }

            //关闭
            outputStream.close();
            fileInputStream.close();

            return R.success("文件加载成功");

        } catch (Exception e) {
            throw new FileEmptyException("文件不存在异常");
        }


    }

}
