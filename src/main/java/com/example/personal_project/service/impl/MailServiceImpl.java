package com.example.personal_project.service.impl;

import com.example.personal_project.model.Mail;
import com.example.personal_project.repository.MailRepo;
import com.example.personal_project.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class MailServiceImpl implements MailService {

    public final MailRepo mailRepo;
    public final MailServerService mailServerService;
    @Value("${product.paging.size}")
    private int pagingSize;

    public MailServiceImpl(MailRepo mailRepo, MailServerService mailServerService) {
        this.mailRepo = mailRepo;
        this.mailServerService = mailServerService;
    }

    @Override
    public void insertBatch(List<Mail> mails) {
        mailRepo.insertBatch(mails);
    }

    @Override
    public void insertOpenRecord(String campaignId, String eventType, String audienceUUID, String recipientMail, String subject) {
        mailRepo.insertEventRecord(campaignId, eventType, audienceUUID, recipientMail, subject);
    }

    @Override
    public Double getMailDeliveryRateForCompany(String account) {
        return mailRepo.getMailDeliveryRateForCompany(account);
    }

    @Override
    public Map<LocalDate, Double> getDailyMailDeliveryRate(Long companyId) {
        return mailRepo.getDailyMailDeliveryRate(companyId);
    }

    @Override
    public Map<LocalDate, Double> trackDailyMailDeliveryRate(String account) {
        return mailRepo.trackDailyMailDeliveryRate(account);
    }

    @Override
    public Map<LocalDate, Double> trackDailyMailDeliveryRateByDate(String account, LocalDate startDate, LocalDate endDate) {
        return mailRepo.trackDailyMailDeliveryRateByDate(account, startDate, endDate);
    }

    @Override
    public Map<String, Integer> calculateMailConversionRate(String account) {
        return mailRepo.calculateMailConversionRate(account);
    }

    @Override
    public Map<String, Integer> calculateMailConversionRateByDate(String account, LocalDate startDate, LocalDate endDate) {
        return mailRepo.calculateMailConversionRateByDate(account, startDate, endDate);
    }

    @Override
    public Map<String, Map<LocalDate, Integer>> analyzeEventPastDays(String account, Integer days) {
        return mailRepo.analyzeEventPastDays(account, days);
    }

    @Override
    public Map<String, Map<LocalDate, Integer>> analyzeEventPastByDate(String account, LocalDate startDate, LocalDate endDate) {
        return mailRepo.analyzeEventPastByDate(account, startDate, endDate);
    }

    @Override
    public List<Map<String, Object>> analyzeCampaignAudienceByAge(Long campaignId) {
        return mailRepo.analyzeCampaignAudienceByAge(campaignId);
    }

    @Override
    public List<Mail> trackFailedMailsByCampaignId(Long campaignId) {
        return mailRepo.trackFailedMailsByCampaignId(campaignId);
    }

    @Override
    public List<Mail> trackFailedMailsByCampaignIdWithPage(Long campaignId, int paging) {
        int offset = paging * pagingSize;
        return mailRepo.trackFailedMailsByCampaignIdWithPage(campaignId, pagingSize + 1, offset);
    }
}
