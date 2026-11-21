package com.farm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.farm.mapper")
public class FarmApplication {

    public static void main(String[] args) {
        SpringApplication.run(FarmApplication.class, args);
    }
}
