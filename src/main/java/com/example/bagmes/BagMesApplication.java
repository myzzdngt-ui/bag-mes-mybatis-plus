package com.example.bagmes;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.bagmes.mapper")
public class BagMesApplication {
    public static void main(String[] args) {
        SpringApplication.run(BagMesApplication.class, args);
    }
}
