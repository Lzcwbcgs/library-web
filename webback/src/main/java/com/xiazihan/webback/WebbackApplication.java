package com.xiazihan.webback;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.xiazihan.webback.mapper")
public class WebbackApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebbackApplication.class, args);
    }

}
