package com.simplemes;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 简易 MES 服务启动类。
 */
@SpringBootApplication
@MapperScan("com.simplemes.**.mapper")
public class SimpleMesApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleMesApplication.class, args);
    }
}
