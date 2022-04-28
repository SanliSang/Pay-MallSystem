package com.sanli.paysystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@EnableRabbit
@SpringBootApplication
@MapperScan(basePackages = "com.sanli.paysystem.dao")
public class PaySystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaySystemApplication.class, args);
    }

}
