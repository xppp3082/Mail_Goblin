package com.example.personal_project.service;

import com.example.personal_project.model.Mail;
import com.example.personal_project.model.MailHook;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface MailService {
    void insertBatch(List<Mail> mails);

    void updateBatchByMimeId(List<Mail> mails);

    void insertOpenRecord(String campaignId, String eventType, String audienceUUID, String recipientMail, String subject);

    Double getMailDeliveryRateForCompany(String account);

    Map<LocalDate, Double> getDailyMailDeliveryRate(Long companyId);

    Map<LocalDate, Double> trackDailyMailDeliveryRate(String account);

    Map<LocalDate, Double> trackDailyMailDeliveryRateByDate(String account, LocalDate startDate, LocalDate endDate);

    Map<String, Integer> calculateMailConversionRate(String account);

    Map<String, Integer> calculateMailConversionRateByDate(String account, LocalDate startDate, LocalDate endDate);

    Map<String, Map<LocalDate, Integer>> analyzeEventPastDays(String account, Integer days);

    //    void insertBatch(List<Mail> mails,Long companyId);
    Map<String, Map<LocalDate, Integer>> analyzeEventPastByDate(String account, LocalDate startDate, LocalDate endDate);

    List<Map<String, Object>> analyzeCampaignAudienceByAge(Long campaignId);

    List<Mail> trackFailedMailsByCampaignId(Long campaignId);

    List<Mail> trackFailedMailsByCampaignIdWithPage(Long campaignId, int paging);

    void insertReceiveRecordWithMailHook(MailHook mailHook);
}

