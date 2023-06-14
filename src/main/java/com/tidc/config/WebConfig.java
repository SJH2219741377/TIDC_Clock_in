package com.tidc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类,配置跨域
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 匹配所有接口路径
                .allowCredentials(true) // 是否发送Cookie
                .allowedOriginPatterns("*") // 设置允许跨域请求的域名和端口号
                .allowedMethods("GET", "POST", "PUT", "DELETE") // 请求方法
                .allowedHeaders("*") // 所有请求头皆可
                .exposedHeaders("*"); // 将所有响应头都暴露给客户端
    }

}
