package com.green.smarty;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SmartyApplication {
	public static void main(String[] args) {
		SpringApplication.run(SmartyApplication.class, args);
	}

}
