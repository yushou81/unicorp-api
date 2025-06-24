package com.csu.linkneiapi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.csu.linkneiapi.mapper")
@SpringBootApplication
public class LinkneiApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(LinkneiApiApplication.class, args);
    }

}
