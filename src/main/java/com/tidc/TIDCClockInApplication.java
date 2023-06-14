package com.tidc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类
 *
 * @author 宋佳豪
 * @version 1.0
 */
@SpringBootApplication
@MapperScan("com.tidc.mapper")
public class TIDCClockInApplication {

    public static void main(String[] args) {
        SpringApplication.run(TIDCClockInApplication.class, args);
    }

}
