package com.example.personal_project.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // 注冊 JavaTimeModule 以支持 Java 8 的日期時間類型
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}
