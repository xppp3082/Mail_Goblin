package com.example.personal_project.component;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.example.personal_project.model.Campaign;
import com.example.personal_project.service.CampaignService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@Slf4j
public class MailPublisher {

    private final AmazonSQS amazonSQSClient;
    private final ObjectMapper objectMapper;
    private final CampaignService campaignService;
    @Value("${aws.queueName}")
    private String queueName;

    public MailPublisher(AmazonSQS amazonSQSClient, ObjectMapper objectMapper, CampaignService campaignService) {
        this.amazonSQSClient = amazonSQSClient;
        this.objectMapper = objectMapper;
        this.campaignService = campaignService;
    }

    //    @Scheduled(cron = "0 0 8 * * ?")
    @Scheduled(cron = "0 0 * * * *")
    public void publishCampaign() {
        try {
            GetQueueUrlResult queueUrlResult = amazonSQSClient.getQueueUrl(queueName);
//            LocalDate targetDate = LocalDate.now();
//            LocalDateTime targetDateTime = LocalDateTime.now();
            LocalDateTime targetDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
            log.info(targetDateTime.toString());
            List<Campaign> campaignsToSend = campaignService.getAllCompletedCampaigns();
//            log.info(campaignsToSend.toString());
            for (Campaign campaign : campaignsToSend) {
                if (campaign.getSendDateTime() == null) continue;
//                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime sendDateTime = LocalDateTime.parse(campaign.getSendDateTime()).truncatedTo(ChronoUnit.HOURS);
                if (sendDateTime.equals(targetDateTime)) {
                    log.info("來自Queue的問候:今天有Campaign要發送喔!! " + campaign.toString());
                    var result = amazonSQSClient.sendMessage(queueUrlResult.getQueueUrl(), objectMapper.writeValueAsString(campaign));
                    log.info(result.toString());
                }
            }
        } catch (Exception e) {
            log.error("Queue Exception Message: {}", e.getMessage());
        }
    }

    public void publishCampaign(Campaign campaign) {
        try {
            GetQueueUrlResult queueUrlResult = amazonSQSClient.getQueueUrl(queueName);
            log.info("寄送測試 " + campaign.toString());
            var result = amazonSQSClient.sendMessage(queueUrlResult.getQueueUrl(), objectMapper.writeValueAsString(campaign));
        } catch (JsonProcessingException e) {
            log.error("Queue Exception Message: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
