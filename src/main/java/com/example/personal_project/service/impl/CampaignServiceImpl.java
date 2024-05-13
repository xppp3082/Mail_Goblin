package com.example.personal_project.service.impl;

import com.example.personal_project.model.*;
import com.example.personal_project.model.status.ExecuteStatus;
import com.example.personal_project.repository.CampaignRepo;
import com.example.personal_project.service.CampaignService;
import com.example.personal_project.service.MailTemplateService;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Slf4j
@Service
public class CampaignServiceImpl implements CampaignService {
    private final MailServerService mailServerService;
    private final MailServiceImpl mailService;
    private final MailTemplateService mailTemplateService;
    private final AudienceServiceImpl audienceService;
    private final CampaignRepo campaignRepo;
    private final MailGunService mailGunService;

    @Value("${product.paging.size}")
    private int pagingSize;

    public CampaignServiceImpl(MailServerService mailServerService, MailServiceImpl mailService, MailTemplateService mailTemplateService, AudienceServiceImpl audienceService, CampaignRepo campaignRepo, MailGunService mailGunService) {
        this.mailServerService = mailServerService;
        this.mailService = mailService;
        this.mailTemplateService = mailTemplateService;
        this.audienceService = audienceService;
        this.campaignRepo = campaignRepo;
        this.mailGunService = mailGunService;
    }

    @Override
    public Campaign findCampaignById(Long id) {
        return campaignRepo.findCampaignById(id);
    }

    @Override
    public List<Campaign> getAllCampaignsByAccount(String account) {
        return campaignRepo.getAllCampaignsByAccount(account);
    }

    @Override
    public List<Campaign> getPageCampaignByAccount(String account, int paging) {
        int offset = paging * pagingSize;
        return campaignRepo.getPageCampaignByAccount(account, pagingSize + 1, offset);
    }

    @Override
    public List<Campaign> getPageCampaignByAccountWithTag(String account, int paging) {
        int offset = paging * pagingSize;
        return campaignRepo.getPageCampaignByAccountWithTag(account, pagingSize + 1, offset);
    }

    @Override
    public Integer getTotalCampaignCountByAccount(String account) {
        return campaignRepo.getTotalCampaignCountByAccount(account);
    }

    @Override
    public List<Campaign> getAllCompletedCampaigns() {
        return campaignRepo.getAllCompletedCampaigns();
    }

    @Override
    public void updateCampaignExecuteStatus(Campaign campaign) {
        campaignRepo.updateCampaignExecuteStatus(campaign);
    }

    @Override
    public void sendCampaign(Campaign campaign) throws MessagingException, UnsupportedEncodingException {
        List<Audience> audiences = audienceService.getAllAudienceByCampaign(campaign);
        MailTemplate mailTemplate = mailTemplateService.getTemplateByCampaign(campaign);
        EmailCampaign emailCampaign = new EmailCampaign(campaign, mailTemplate, audiences);
        try {
            List<Mail> mails = mailServerService.sendBatchMails2(emailCampaign);
//            mailService.insertBatch(mails);
            mailService.updateBatchByMimeId(mails);
            log.info("batch insert mail record successfully");
        } catch (Exception e) {
            log.error("batch insert email record fail : " + e.getMessage());
        }
    }

    @Override
    public void sendCampaignById(Long id) throws MessagingException, UnsupportedEncodingException {
        Campaign targetCampaign = findCampaignById(id);
        sendCampaign(targetCampaign);
    }

    @Override
    public Campaign insertNewCampaign(Campaign campaign, String status) throws Exception {
//        //將前端設定的時區轉換為UTC+0的時區
//        assert campaign.getSendDateTime() != null;
//        String sendDateTimeString = campaign.getSendDateTime().replace(' ', 'T');
//
//        // 使用 DateTimeFormatter 將字符串解析為 LocalDateTime 對象
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        LocalDateTime localDateTime = LocalDateTime.parse(sendDateTimeString);
//
//        // 將 LocalDateTime 對象轉換為 UTC+0 時間
//        LocalDateTime utcDateTime = localDateTime.atOffset(ZoneOffset.UTC).toLocalDateTime();
//
//        // 將轉換後的 UTC+0 時間格式化為字符串，並設置回 campaign 中
//        campaign.setSendDateTime(utcDateTime.format(formatter));
        // 检查 Campaign 对象中是否有 null 值的字段

        campaign.setStatus(status);
        campaign.setExecuteStatus(ExecuteStatus.PENDING.name());
        log.info(campaign.toString());
        return campaignRepo.insertNewCampaign(campaign);
    }

    @Override
    public void deleteCampaign(Long id) throws Exception {
        campaignRepo.deleteCampaign(id);
    }

//    //根據Campaign的時稱安排進行自動排程寄送
//    //目前每天早上八點檢查一次所有的Campaign
//    //EC2時區和台灣時區不同
////    @Scheduled(cron = "0 0 8 * * ?")
//    public void sendScheduledCampaign() {
//        //取得DB中所有的Campaign
//        log.info("Scheduled campaigns start!!");
//        Date currentDate = new Date();
//        LocalDate targetDate = LocalDate.now();
//        List<Campaign> campaignsToSend = getAllCompletedCampaigns();
//        for (Campaign campaign : campaignsToSend) {
//            log.info("campaign 預計要寄送的日期是: " + campaign.getSendDate());
//            if (campaign.getSendDate().equals(targetDate)) {
//                log.info("今天有Campaign要發送喔!! " + campaign.toString());
//                List<Audience> audiences = audienceService.getAllAudienceByCampaign(campaign);
//                MailTemplate mailTemplate = mailTemplateService.getTemplateByCampaign(campaign);
//                EmailCampaign emailCampaign = new EmailCampaign(campaign, mailTemplate, audiences);
//                try {
//                    List<Mail> mails = mailServerService.sendBatchMails2(emailCampaign);
//                    mailService.insertBatch(mails);
//                    updateCampaignExecuteStatus(campaign);
//                    log.info("batch insert mail record successfully");
//                } catch (Exception e) {
//                    log.error("batch insert email record fail : " + e.getMessage());
//                }
//            }
//        }
//    }

}
