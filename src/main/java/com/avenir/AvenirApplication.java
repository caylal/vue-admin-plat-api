package com.avenir;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Configuration;

@ServletComponentScan(basePackages = "com.avenir")
@SpringBootApplication
@MapperScan("com.avenir.mapper")
@EnableAutoConfiguration
@Configuration
public class AvenirApplication {

	public static void main(String[] args) {
		SpringApplication.run(AvenirApplication.class, args);
	}

}
