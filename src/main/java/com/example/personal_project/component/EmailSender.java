package com.example.personal_project.component;

import com.example.personal_project.model.Campaign;
import com.example.personal_project.model.EmailCampaign;
import com.example.personal_project.service.CampaignService;
import com.example.personal_project.service.impl.CampaignServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

//@Component
//@Slf4j
//public class EmailSender {
//
//    @Autowired
//    private AmqpTemplate amqpTemplate;
//
//    private final CampaignService campaignService;
//
//    public EmailSender(CampaignService campaignService) {
//        this.campaignService = campaignService;
//    }
//
////    @Scheduled(cron = "20 42 9 * * ?")
//    public void sendScheduledCampaign(){
//        //取得DB中所有的Campaign
//        log.info("Scheduled campaigns start!!");
//        LocalDate targetDate = LocalDate.now();
//        List<Campaign> campaignsToSend = campaignService.getAllCompletedCampaigns();
//        for(Campaign campaign:campaignsToSend){
//            log.info("來自Queue的問候，今天有Campaign要發送喔!! " + campaign.toString());
//            if(campaign.getSendDate().equals(targetDate)){
//                try{
//                    //將Campaign的發送送到Queue中
//                    log.info("Queue:今天有Campaign要發送喔!! " + campaign.toString());
//                    amqpTemplate.convertAndSend("campaignQueue",campaign);
//                    log.info("Email request sent to queue for campaign: " + campaign.getId());
//                }catch (Exception e){
//                    log.error("Failed to send email request to queue for campaign "
//                            + campaign.getId() + ": " + e.getMessage());
//                }
//            }
//        }
//    }
//}
