package com.example.personal_project.service.impl;

import com.example.personal_project.repository.MailRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailCampaignService {

    private final MailServerService mailServerService;
    private final MailRepo mailRepo;

    public EmailCampaignService(MailServerService mailServerService, MailRepo mailRepo) {
        this.mailServerService = mailServerService;
        this.mailRepo = mailRepo;
    }

}
