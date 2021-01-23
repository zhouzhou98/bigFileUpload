package com.file.bigfile;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.file.bigfile.mapper")
public class BigfileApplication {

    public static void main(String[] args) {
        SpringApplication.run(BigfileApplication.class, args);
    }

}
