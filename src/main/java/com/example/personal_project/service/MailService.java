package com.example.personal_project.service;

import com.example.personal_project.model.Mail;

import java.util.List;

public interface MailService {
    void insertBatch(List<Mail> mails);
//    void insertBatch(List<Mail> mails,Long companyId);
}
