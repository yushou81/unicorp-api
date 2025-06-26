package com.csu.unicorp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.csu.unicorp.mapper")
@SpringBootApplication
public class UnicorpApplication {

    public static void main(String[] args) {
        SpringApplication.run(UnicorpApplication.class, args);
    }

}
