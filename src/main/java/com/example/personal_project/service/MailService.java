package com.example.personal_project.service;

import com.example.personal_project.model.Mail;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface MailService {
    void insertBatch(List<Mail> mails);
    void insertOpenRecord(String campaignId,String eventType,String audienceUUID);
    Double getMailDeliveryRateForCompany(String account);
    Map<LocalDate,Double>getDailyMailDeliveryRate(Long companyId);
    Map<LocalDate,Double>trackDailyMailDeliveryRate(String account);
    Map<String,Integer> calculateMailConversionRate(String account);
    Map<String,Map<LocalDate,Integer>>analyzeEventPastDays(String account,Integer days);
//    void insertBatch(List<Mail> mails,Long companyId);
}
