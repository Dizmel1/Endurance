package com.example.Endurance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class EnduranceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnduranceApplication.class, args);
	}

}
