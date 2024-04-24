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

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class MailPublisher {

    @Value("${aws.queueName}")
    private String queueName;

    private final AmazonSQS amazonSQSClient;
    private final ObjectMapper objectMapper;
    private final CampaignService campaignService;

    public MailPublisher(AmazonSQS amazonSQSClient, ObjectMapper objectMapper, CampaignService campaignService) {
        this.amazonSQSClient = amazonSQSClient;
        this.objectMapper = objectMapper;
        this.campaignService = campaignService;
    }

//    @Scheduled(cron = "0 0 8 * * ?")
    public void publishCampaign(){
        try {
            GetQueueUrlResult queueUrlResult = amazonSQSClient.getQueueUrl(queueName);
            LocalDate targetDate = LocalDate.now();
            List<Campaign> campaignsToSend = campaignService.getAllCompletedCampaigns();
            for(Campaign campaign:campaignsToSend){
//                log.info("來自Queue的問候: campaign 預計要寄送的日期是 "+campaign.getSendDate());
                if(campaign.getSendDate().equals(targetDate)) {
                    log.info("來自Queue的問候:今天有Campaign要發送喔!! " + campaign.toString());
                    var result = amazonSQSClient.sendMessage(queueUrlResult.getQueueUrl(),objectMapper.writeValueAsString(campaign));
                    log.info(result.toString());
                }
            }
        }catch (Exception e){
            log.error("Queue Exception Message: {}", e.getMessage());
        }
    }

    public void publishCampaign(Campaign campaign){
        try{
            GetQueueUrlResult queueUrlResult = amazonSQSClient.getQueueUrl(queueName);
            log.info("寄送測試 " + campaign.toString());
            var result = amazonSQSClient.sendMessage(queueUrlResult.getQueueUrl(),objectMapper.writeValueAsString(campaign));
        } catch (JsonProcessingException e) {
            log.error("Queue Exception Message: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
