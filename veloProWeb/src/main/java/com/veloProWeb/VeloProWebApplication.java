package com.veloProWeb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VeloProWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(VeloProWebApplication.class, args);
	}

}
