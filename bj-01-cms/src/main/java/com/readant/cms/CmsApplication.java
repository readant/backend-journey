package com.readant.cms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 兴华小组官网 - 启动类
 *
 * 这是整个项目的"总开关"。
 * @SpringBootApplication 是一个"便利贴"，贴在类上告诉 Spring：
 * "这是一个 Spring Boot 应用，帮我自动配置好所有东西"
 *
 * @MapperScan 告诉 MyBatis-Plus："Mapper 接口都在这个包下面，帮我找到它们"
 *
 * main 方法是程序的入口，就像电灯开关——按下去，整个项目就启动了。
 */
@SpringBootApplication
@MapperScan("com.readant.cms.mapper")
public class CmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CmsApplication.class, args);
    }
}
