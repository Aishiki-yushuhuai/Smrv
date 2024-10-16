package com.shelley;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.shelley.dao")
public class SmrvpApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmrvpApplication.class, args);
    }


}
