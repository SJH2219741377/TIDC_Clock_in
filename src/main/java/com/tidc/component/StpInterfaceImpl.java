package com.tidc.component;

import cn.dev33.satoken.stp.StpInterface;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tidc.entity.SuperAdmin;
import com.tidc.entity.User;
import com.tidc.handler.constant.TidcConstant;
import com.tidc.mapper.SuperAdminMapper;
import com.tidc.mapper.UserMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义权限验证接口扩展, 用于自定义用户角色和权限
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    @Resource
    SuperAdminMapper superAdminMapper;

    @Resource
    UserMapper userMapper;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return null;
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        List<String> list = new ArrayList<>();
        SuperAdmin superAdmin = superAdminMapper.selectOne(Wrappers.<SuperAdmin>lambdaQuery().eq(SuperAdmin::getAccount, loginId));
        if (ObjectUtil.isNotEmpty(superAdmin)) { // 如果是超管,则赋予超级管理员的角色
            list.add(TidcConstant.SUPER_ADMIN);
            return list;
        }
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getOpenId, loginId));
        if (TidcConstant.ADMIN.equals(user.getRole())) {
            list.add(TidcConstant.ADMIN);
        } else if (TidcConstant.NORMAL.equals(user.getRole())) {
            list.add(TidcConstant.NORMAL);
        }
        return list;
    }

}
