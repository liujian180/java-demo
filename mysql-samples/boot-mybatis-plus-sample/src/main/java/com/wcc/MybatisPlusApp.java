package com.wcc;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类
 * wcc 2022/7/26
 */
@SpringBootApplication
@Slf4j
@MapperScan("com.wcc.scada.core.mapper")
public class MybatisPlusApp implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(MybatisPlusApp.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("mybatis plus start");
    }
}
