package com.example.personal_project.component;

import com.example.personal_project.service.impl.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("consumer")
@Slf4j
public class RedisMailConsumerTask {
    private final RedisService redisService;

    public RedisMailConsumerTask(RedisService redisService) {
        this.redisService = redisService;
    }

    @Scheduled(fixedDelay = 10000)
    public void checkAndUpdateRedisMail() {
        redisService.checkAndUpdateRedisMail();
    }
}
