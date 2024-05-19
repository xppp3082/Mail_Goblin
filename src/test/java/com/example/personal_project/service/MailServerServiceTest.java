package com.example.personal_project.service;

import com.example.personal_project.model.*;
import com.example.personal_project.model.status.DeliveryStatus;
import com.example.personal_project.service.impl.MailServerService;
import com.example.personal_project.service.impl.RedisService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class MailServerServiceTest {

    @InjectMocks
    private MailServerService mailServerService;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine htmlTemplataEngine;

    @Mock
    private AudienceService audienceService;

    @Mock
    private RedisService redisService;

    @Mock
    private Environment environment;

    @BeforeEach
    public void setUp() {
        MockEnvironment mockEnvironment = new MockEnvironment();
        mockEnvironment.setProperty("spring.mail.properties.mail.smtp.from", "test@example.com");
        mockEnvironment.setProperty("mail.from.name", "Identity");
        when(environment.getProperty("spring.mail.properties.mail.smtp.from")).thenReturn("test@example.com");
        when(environment.getProperty("mail.from.name", "Identity")).thenReturn("Identity");
    }

    @Test
    public void testSendBatchMails_Success() throws MessagingException, UnsupportedEncodingException {
        //Prepare Test Data
        Audience audience = new Audience();
        audience.setId(1L);
        audience.setEmail("test@example.com");
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
        emailCampaign.setAudiences(Arrays.asList(audience));
        emailCampaign.setMailTemplate(mailTemplate);
        emailCampaign.setCampaign(campaign);

        //Mocking Behavior
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mimeMessage.getMessageID()).thenReturn("<1234567890@example.com>");
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(htmlTemplataEngine.process(eq("registration"), any(Context.class))).thenReturn("Processed Template");

        //Execute the method
        List<Mail> result = mailServerService.sendBatchMails(emailCampaign);

        //Verify
        assertNotNull(result);
        assertEquals(1, result.size());
        Mail sentMail = result.get(0);
        assertEquals("test@example.com", sentMail.getRecipientMail());
        assertEquals("Campaign Subject", sentMail.getSubject());
        assertEquals(DeliveryStatus.RECEIVE.name(), sentMail.getStatus());

        verify(mailSender, times(1)).send(mimeMessage);
        verify(audienceService, times(1)).updateMailCount(audience.getAudienceUUID());
        verify(redisService, times(1)).updateMailFromSpringboot(any(RedisMail.class));
    }

}
