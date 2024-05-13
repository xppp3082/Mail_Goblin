package com.example.personal_project.service.impl;

import com.example.personal_project.model.RedisMail;
import com.example.personal_project.repository.MailRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class RedisService {
    //    private final RedisTemplate<String, Mail> redisTemplate;
    private final RedisTemplate<String, RedisMail> redisTemplate;
    private final MailRepo mailRepo;
    private final HashOperations<String, String, RedisMail> hashOperations;

    public RedisService(RedisTemplate<String, RedisMail> redisTemplate, MailRepo mailRepo) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
        this.mailRepo = mailRepo;
    }


    public void updateMailFromWebhook(RedisMail mail) {
        String mimeID = mail.getMimeID();
        if (mimeID != null) {
            if (!hashOperations.hasKey(mimeID, "mail")) {
                mail.setMailHookUpdate(true);
                hashOperations.put(mimeID, "mail", mail);
                redisTemplate.expire(mimeID, Duration.ofMinutes(10));
                log.info("add mail to redis by mailgun webhook !");
            } else {
                RedisMail existingMail = hashOperations.get(mimeID, "mail");
//                existingMail.setCampaignID(mail.getCampaignID());
//                existingMail.setTimestamp(mail.getTimestamp());
//                existingMail.setAudienceID(mail.getAudienceID());
                existingMail.setRecipientMail(mail.getRecipientMail());
                existingMail.setSubject(mail.getSubject());
                existingMail.setStatus(mail.getStatus());
                existingMail.setMailHookUpdate(true);
                hashOperations.put(mimeID, "mail", existingMail);
                log.info("update mail to redis by mailgun webhook !");
            }
        }
    }


    public void updateMailFromSpringboot(RedisMail mail) {
        String mimeID = mail.getMimeID();
        try {
            if (mimeID != null) {
                mail.setSpringBootUpdate(true);
                if (!hashOperations.hasKey(mimeID, "mail")) {
                    hashOperations.put(mimeID, "mail", mail);
                    redisTemplate.expire(mimeID, Duration.ofMinutes(10));
                    log.info("add mail to redis by springboot !");
                } else {
                    RedisMail existingMail = hashOperations.get(mimeID, "mail");
                    existingMail.setCampaignID(mail.getCampaignID());
                    existingMail.setTimestamp(mail.getTimestamp());
                    existingMail.setAudienceID(mail.getAudienceID());
                    existingMail.setSpringBootUpdate(true);
                    hashOperations.put(mimeID, "mail", existingMail);
                    log.info("update mail to redis by springboot !");
                }
            }
        } catch (Exception e) {
            log.error("Error on update mail to Redis : " + e.getMessage());
        }
    }

    //    @Scheduled(fixedDelay = 10000)
    public void checkAndUpdateRedisMail() {
//        log.info("Start cleaning Redis Mail !");
        try {
            Set<String> keys = redisTemplate.keys("*");
            List<RedisMail> mailsCompleted = new ArrayList<>();
            assert keys != null;
            for (String key : keys) {
                RedisMail mail = hashOperations.get(key, "mail");
                if (mail != null && mail.getSpringBootUpdate() != null && mail.getMailHookUpdate() != null
                        && mail.getSpringBootUpdate() && mail.getMailHookUpdate()) {
                    mailsCompleted.add(mail);
                }
            }
            if (mailsCompleted.size() > 0) {
                mailRepo.batchInsertRedisMail(mailsCompleted);
                for (RedisMail mail : mailsCompleted) {
                    hashOperations.delete(mail.getMimeID(), "mail");
                }
                log.info("delete " + mailsCompleted.size() + " completed mails from Redis.");
            }
        } catch (Exception e) {
            log.warn("Error on checking and updating Redis data: " + e.getMessage());
        }
    }

}
