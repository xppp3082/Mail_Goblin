package com.example.personal_project.component;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.example.personal_project.model.Audience;
import com.example.personal_project.model.Campaign;
import com.example.personal_project.model.EmailCampaign;
import com.example.personal_project.model.Mail;
import com.example.personal_project.service.AudienceService;
import com.example.personal_project.service.CampaignService;
import com.example.personal_project.service.MailService;
import com.example.personal_project.service.impl.CampaignServiceImpl;
import com.example.personal_project.service.impl.MailServerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class MailConsumer {
    @Value("${aws.queueName}")
    private String queueName;
    @Value("${aws.queue.url}")
    private String queueURL;
    private final AmazonSQS amazonSQSClient;
    private final ObjectMapper objectMapper;
    private  final AudienceService audienceService;
    private final MailService mailService;
    private final MailServerService mailServerService;
    private final CampaignService campaignService;

    public MailConsumer(AmazonSQS amazonSQSClient, ObjectMapper objectMapper, AudienceService audienceService, MailService mailService, MailServerService mailServerService, CampaignService campaignService) {
        this.amazonSQSClient = amazonSQSClient;
        this.objectMapper = objectMapper;
        this.audienceService = audienceService;
        this.mailService = mailService;
        this.mailServerService = mailServerService;
        this.campaignService = campaignService;
    }

//    @Scheduled(fixedDelay = 5000)
//    @SqsListener("${aws.queueName}")
    public void consumeCampaign(){
        try{
            String queueUrl = amazonSQSClient.getQueueUrl(queueName).getQueueUrl();
            ReceiveMessageResult receiveMessageResult = amazonSQSClient.receiveMessage(queueUrl);
            if(!receiveMessageResult.getMessages().isEmpty()){
                Message message = receiveMessageResult.getMessages().get(0);

                // Check if the message is visible
                if (message.getAttributes().containsKey("ApproximateReceiveCount")) {
                    int receiveCount = Integer.parseInt(message.getAttributes().get("ApproximateReceiveCount"));
                    if (receiveCount > 1) {
                        log.info("Message received multiple times. Skipping processing.");
                        return;
                    }
                }
                log.info("Read Message from queue: {}", message.getBody());
                //Parse JSON message
                JsonNode jsonNode = objectMapper.readTree(message.getBody());
                //提取特定的值
                if (jsonNode.has("id")) {
                    String id = jsonNode.get("id").asText();
                    log.info("ID: {}", id);
                } else {
                    log.warn("Message does not contain 'id' key.");
                }
                amazonSQSClient.deleteMessage(queueUrl, message.getReceiptHandle());
            }else {
                log.info("There's nothing in queue, Mission Complete !");
            }
        }catch (Exception e){
            log.error("Queue Exception Message: {}", e.getMessage());
        }
    }

    public void consumeCampaignLongPoll() throws JsonProcessingException {
        log.info("log polling start!");
        String queueUrl = amazonSQSClient.getQueueUrl(queueName).getQueueUrl();
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest()
                .withQueueUrl(queueURL)
                .withWaitTimeSeconds(20);//use long poliing with waiting time of 20 second
        while(true){
            //receive message
            for (Message message :amazonSQSClient.receiveMessage(receiveMessageRequest).getMessages()){
                    // Check if the message is visible
                    if (message.getAttributes().containsKey("ApproximateReceiveCount")) {
                        int receiveCount = Integer.parseInt(message.getAttributes().get("ApproximateReceiveCount"));
                        if (receiveCount > 1) {
                            log.info("Message received multiple times. Skipping processing.");
                            return;
                        }
                    }
                    log.info("Read Message from queue: {}", message.getBody());
                    //Parse JSON message
                    JsonNode jsonNode = objectMapper.readTree(message.getBody());
                    //提取特定的值
//                String id = jsonNode.get("id").asText();
                    // Check if the necessary keys exist before extracting their values
                    if (jsonNode.has("id")) {
                        // Convert JSON message to Campaign object
                        //String id = jsonNode.get("id").asText();
                        //log.info("ID: {}", id);
                        Campaign campaign = objectMapper.treeToValue(jsonNode, Campaign.class);
                        log.info("Campaign ID: {}", campaign.getId());
                        log.info("Template ID: {}", campaign.getTemplateId());
                        log.info("Tag ID: {}", campaign.getTagId());
                        log.info("Automation ID: {}", campaign.getAutomationId());
                        log.info("Execute Status: {}", campaign.getExecuteStatus());
                        List<Audience> audiences = audienceService.getAllAudienceByCampaign(campaign);
                        EmailCampaign emailCampaign = new EmailCampaign(campaign,audiences);
                        try{
                            List<Mail> mails =  mailServerService.sendBatchMails2(emailCampaign);
                            mailService.insertBatch(mails);
                            campaignService.updateCampaignExecuteStatus(campaign);
                            log.info("batch insert mail record successfully with campaignID :"+campaign.getId());
                        }catch (Exception e){
                            log.error("batch insert email record fail : "+e.getMessage());
                        }
                    } else {
                        log.warn("Message does not contain 'id' key.");
                    }
                    amazonSQSClient.deleteMessage(queueUrl, message.getReceiptHandle());
            }
        }
    }
}
