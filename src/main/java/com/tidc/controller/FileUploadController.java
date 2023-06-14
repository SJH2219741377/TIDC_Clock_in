package com.tidc.controller;

import com.tidc.handler.ResultData;
import com.tidc.utils.FileUploadUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.io.IOException;

@RestController
@RequestMapping("/avatar")
@Validated
public class FileUploadController {
    @Resource
    FileUploadUtil fileUploadUtil;

    @PostMapping("/upload")
    public ResultData<String> upload(@RequestParam("file") MultipartFile file,
                                     @NotBlank(message = "openId不能为空") String openId) throws IOException {
        return fileUploadUtil.uploadAvatar(file, openId);
    }

}
