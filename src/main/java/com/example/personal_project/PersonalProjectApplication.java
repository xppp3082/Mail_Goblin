package com.example.personal_project;

import com.example.personal_project.component.MailConsumer;
import com.example.personal_project.component.MailPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDate;
import java.util.Date;

@Slf4j
@EnableScheduling
@SpringBootApplication
//@SpringBootApplication(exclude = { ThymeleafAutoConfiguration.class })
@OpenAPIDefinition(
		info = @Info(
				title = "Batch#23 Personal Project: Mail Goblin",
				version = "1.0.0",
				description = "This api document is for Appworks Batch#23 personal project of Travis.",
				termsOfService = "runcodenow"
		)
)
public class PersonalProjectApplication {

	private  final MailConsumer mailConsumer;

    public PersonalProjectApplication(MailConsumer mailConsumer) {
        this.mailConsumer = mailConsumer;
    }

    public static void main(String[] args) throws JsonProcessingException {
		ConfigurableApplicationContext context = SpringApplication.run(PersonalProjectApplication.class, args);
		Date currentDate = new Date();
		log.info(currentDate.toString());
		LocalDate currentDateLocal = LocalDate.now();
		log.info(currentDateLocal.toString());
//		PersonalProjectApplication app = context.getBean(PersonalProjectApplication.class);
//		app.mailConsumer.consumeCampaignLongPoll();
	}
//	@Bean
//	public ApplicationRunner runner(MailPublisher publisher) {
//		return args -> {
//			Thread.sleep(3000);
//			publisher.publishCampaign();
//		};
//	}
}
