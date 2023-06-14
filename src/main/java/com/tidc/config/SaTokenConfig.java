package com.tidc.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 路由拦截器,用于拦截未登录的请求和角色校验
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    //  注册路由拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handler -> {
            // 登录校验 - 拦截所有path,排除登录接口
            SaRouter
                    .match("/**")
                    .notMatch("/user/wxLogin/*", "/admin/super/login", "/user/register/*") // 排除的path
                    .check(r -> StpUtil.checkLogin()); // 校验登录

            // 角色校验 - 拦截以 admin/super 开头的路由，必须具备 super 角色才可以通过认证
            SaRouter.match("/admin/super/**", "/admin/super/login", r -> StpUtil.checkRole("super"));
            // 必须具备 admin 角色或者 super 角色才可以通过认证
            SaRouter.match("/admin/**", "/admin/super/login", r -> StpUtil.checkRoleOr("super", "admin"));
            // 必须具备 admin 角色或者 normal 角色才可以通过认证
            SaRouter.match("/user/**", "/user/wxLogin/*", r -> StpUtil.checkRoleOr("admin", "normal"));

        })).addPathPatterns("/**");
    }

}
