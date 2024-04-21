package com.example.personal_project.service.impl;

import com.example.personal_project.model.Mail;
import com.example.personal_project.repository.MailRepo;
import com.example.personal_project.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class MailServiceImpl implements MailService {

    public final MailRepo mailRepo;
    public final MailServerService mailServerService;

    public MailServiceImpl(MailRepo mailRepo, MailServerService mailServerService) {
        this.mailRepo = mailRepo;
        this.mailServerService = mailServerService;
    }

    @Override
    public void insertBatch(List<Mail> mails) {
        mailRepo.insertBatch(mails);
    }

    @Override
    public void insertOpenRecord(String campaignId, String eventType,String audienceUUID) {
        mailRepo.insertEventRecord(campaignId, eventType,audienceUUID);
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
}
