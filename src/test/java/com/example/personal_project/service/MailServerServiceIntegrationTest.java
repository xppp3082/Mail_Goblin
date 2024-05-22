package com.example.personal_project.service;

import com.example.personal_project.model.*;
import com.example.personal_project.model.status.DeliveryStatus;
import com.example.personal_project.service.impl.MailServerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@SpringBootTest
public class MailServerServiceIntegrationTest {

    @Autowired
    private MailServerService mailServerService;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private TemplateEngine htmlTemplateEngine;

    @BeforeEach
    public void setUp() {

    }

    @Test
    public void testSendBatchMails_Integration() throws Exception {
        Audience audience = new Audience();
        audience.setId(1L);
        audience.setEmail("xppp3081@gmail.com");
        audience.setName("Test User");
        audience.setAudienceUUID("123456");

        MailTemplate mailTemplate = new MailTemplate();
        mailTemplate.setSubject("Test Subject");
        mailTemplate.setContent("Test Content");
        mailTemplate.setUrl("http://example.com");

        Campaign campaign = new Campaign();
        campaign.setId(12L);
        campaign.setSubject("Campaign Subject");

        EmailCampaign emailCampaign = new EmailCampaign();
        emailCampaign.setAudiences(List.of(audience));
        emailCampaign.setMailTemplate(mailTemplate);
        emailCampaign.setCampaign(campaign);

        //Execute the method
        List<Mail> result = mailServerService.sendBatchMails(emailCampaign);

        //Verify
        assertNotNull(result);
        assertEquals(1, result.size());
        Mail sentMail = result.get(0);
        assertEquals("xppp3081@gmail.com", sentMail.getRecipientMail());
        assertEquals("Campaign Subject", sentMail.getSubject());
        assertEquals(DeliveryStatus.RECEIVE.name(), sentMail.getStatus());
    }

}
