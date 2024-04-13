package com.example.personal_project;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDate;
import java.util.Date;

@Slf4j
@EnableScheduling
@SpringBootApplication(exclude = { ThymeleafAutoConfiguration.class })
public class PersonalProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(PersonalProjectApplication.class, args);
		Date currentDate = new Date();
		log.info(currentDate.toString());
		LocalDate currentDateLocal = LocalDate.now();
		log.info(currentDateLocal.toString());
	}

}
