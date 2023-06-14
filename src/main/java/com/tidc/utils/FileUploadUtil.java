package com.tidc.utils;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.log.level.Level;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tidc.entity.SuperAdmin;
import com.tidc.entity.User;
import com.tidc.handler.ResultData;
import com.tidc.handler.constant.TidcConstant;
import com.tidc.handler.emun.ReturnCode;
import com.tidc.handler.exception.ServiceException;
import com.tidc.handler.exception.UserNotExistException;
import com.tidc.mapper.SuperAdminMapper;
import com.tidc.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Component
public class FileUploadUtil {

    @Value("${avatar.url}")
    private String fileUrl;

    @Value("${public-domain.url}")
    private String domainName;

    @Resource
    private UserMapper userMapper;
    @Resource
    private SuperAdminMapper superAdminMapper;

    /**
     * 上传头像,上传成功后,会将用户的旧头像删除
     *
     * @param file   图片文件
     * @param openId 用户唯一标识（用户和普通管理员使用openId, 超管使用登录账号）
     * @return 上传成功
     * @throws IOException 写入异常
     */
    @Transactional
    public ResultData<String> uploadAvatar(MultipartFile file, String openId) throws IOException {
        if (ObjectUtil.isEmpty(file)) {
            throw new ServiceException(ReturnCode.FILE_NOT_EMPTY.getCode(), ReturnCode.FILE_NOT_EMPTY.getMessage());
        }
        if (StpUtil.hasRole(TidcConstant.SUPER_ADMIN)) {
            SuperAdmin superAdmin = superAdminMapper.selectOne(Wrappers.<SuperAdmin>lambdaQuery().eq(SuperAdmin::getAccount, openId));
            if (ObjectUtil.isEmpty(superAdmin)) {
                throw new UserNotExistException();
            }
            String avatar = upload(file);
            deleteFile(superAdmin.getAvatar());
            superAdminMapper.update(null, Wrappers.<SuperAdmin>lambdaUpdate()
                    .eq(SuperAdmin::getAccount, openId)
                    .set(SuperAdmin::getAvatar, avatar));
        } else {
            User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getOpenId, openId));
            if (ObjectUtil.isEmpty(user)) {
                throw new UserNotExistException();
            }
            String avatar = upload(file);
            deleteFile(user.getAvatar());
            userMapper.update(null, Wrappers.<User>lambdaUpdate()
                    .eq(User::getOpenId, openId)
                    .set(User::getAvatar, avatar));
        }
        return ResultData.success("上传成功");
    }

    /**
     * 上传图片
     *
     * @param file 图片文件
     * @return 文件访问路径
     * @throws IOException 写入失败
     */
    private String upload(MultipartFile file) throws IOException {

        if (!Objects.equals(file.getContentType(), "image/png") && !Objects.equals(file.getContentType(), "image/jpeg")) {
            throw new ServiceException(ReturnCode.FILE_FORMAT_ERROR.getCode(), ReturnCode.FILE_FORMAT_ERROR.getMessage());
        }

        // 原始名称
        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null) {
            throw new ServiceException(ReturnCode.NOT_FOUND.getCode(), "上传文件的文件名不能为空");
        }

        // 文件扩展名
        String type = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 存储到本地
        String filename = UUID.randomUUID() + type;
        String filePath = fileUrl + filename;
        File dest = new File(filePath);

        if (!dest.exists()) {
            FileUtil.mkParentDirs(dest);
        }

        file.transferTo(dest);

        // 返回头像访问地址
        return domainName + filename;
    }

    /**
     * 删除指定路径下的文件
     *
     * @param path 文件路径
     */
    private void deleteFile(String path) {
        // 判断文件是否存在
        if (FileUtil.exist(path)) {
            // 删除文件
            boolean del = FileUtil.del(path);
            if (del) {
                Console.log(Level.INFO, "文件删除成功");
            } else {
                Console.log(Level.INFO, "文件删除失败");
            }
        } else {
            Console.log(Level.WARN, "文件不存在");
        }
    }

}
