package com.example.personal_project.service.impl;

import com.example.personal_project.model.Campaign;
import com.example.personal_project.model.Mail;
import com.example.personal_project.repository.MailRepo;
import com.example.personal_project.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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

}
