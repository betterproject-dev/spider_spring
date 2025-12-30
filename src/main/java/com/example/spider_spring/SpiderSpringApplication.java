package com.example.spider_spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpiderSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpiderSpringApplication.class, args);
	}

}
