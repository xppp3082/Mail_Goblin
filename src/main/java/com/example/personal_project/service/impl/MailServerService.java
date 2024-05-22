package com.example.personal_project.service.impl;

import com.example.personal_project.model.*;
import com.example.personal_project.model.status.DeliveryStatus;
import com.example.personal_project.service.AudienceService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.mail.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class MailServerService {

    private static final String TEMPLATE_NAME = "registration";
    private static final String SPRING_LOGO_IMAGE = "templates/images/logo.png";
    private final Environment environment;
    private final JavaMailSender mailSender;
    private final TemplateEngine htmlTemplateEngine;
    private final AudienceService audienceService;
    private final RedisService redisService;
    @Value("${mailgoblin.track.open.url}")
    private String openTrackBaseUrl;
    @Value("${mailgoblin.track.click.url}")
    private String clickTrackBaseUrl;

    public MailServerService(Environment environment, JavaMailSender mailSender, TemplateEngine htmlTemplateEngine, AudienceService audienceService, RedisService redisService) {
        this.environment = environment;
        this.mailSender = mailSender;
        this.htmlTemplateEngine = htmlTemplateEngine;
        this.audienceService = audienceService;
        this.redisService = redisService;
    }


    public List<Mail> sendBatchMails(EmailCampaign emailCampaign) {
        List<Mail> mails = new ArrayList<>();
        try {
            //get mailTemplate from mail Campaign
            MailTemplate mailTemplate = emailCampaign.getMailTemplate();
            Campaign campaign = emailCampaign.getCampaign();
            //generate click URL and open URL
            for (Audience audience : emailCampaign.getAudiences()) {
                MimeMessage mimeMessage = createTrackingMail(mailTemplate, audience, campaign);
                Mail mail = createMail(campaign, audience);
                sendMimeMessage(mimeMessage, mail, audience);
                mails.add(mail);
                //insert mail data to redis
                updateRedisMailFromSpringBoot(mail);
            }
        } catch (Exception e) {
            log.error("Error sending batch mails", e);
        }
        return mails;
    }

    //組裝 mime 格式 email 所需要的資訊
    public MimeMessage createTrackingMail(MailTemplate mailTemplate, Audience audience, Campaign campaign) throws MessagingException, UnsupportedEncodingException {
        String confirmationUrl = mailTemplate.getUrl();
        log.info(audience.getEmail());
        String openTrackUrl = String.format("%s?UID=%s&CID=%s&recipient=%s&subject=%s",
                openTrackBaseUrl,
                audience.getAudienceUUID(),
                campaign.getId(),
                audience.getEmail(),
                campaign.getSubject());
        String clickTrackUrl = String.format("%s?UID=%s&CID=%s&cusWeb=%s&recipient=%s&subject=%s",
                clickTrackBaseUrl,
                audience.getAudienceUUID(),
                campaign.getId(),
                confirmationUrl,
                audience.getEmail(),
                campaign.getSubject());
        log.info(openTrackUrl + clickTrackUrl);
        String mailFrom = environment.getProperty("spring.mail.properties.mail.smtp.from");
        String mailFromName = environment.getProperty("mail.from.name", "Identity");
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper email;
        email = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        email.setTo(audience.getEmail());
        email.setSubject(campaign.getSubject());
        email.setFrom(new InternetAddress(mailFrom, mailFromName));

        final Context ctx = new Context(LocaleContextHolder.getLocale());
        ctx.setVariable("subject", mailTemplate.getSubject());
        ctx.setVariable("email", audience.getEmail());
        ctx.setVariable("content", mailTemplate.getContent());
        ctx.setVariable("name", audience.getName());
        ctx.setVariable("springLogo", SPRING_LOGO_IMAGE);
        ctx.setVariable("url", clickTrackUrl);
        ctx.setVariable("endpoint", openTrackUrl);
        final String htmlContent = this.htmlTemplateEngine.process(TEMPLATE_NAME, ctx);
        email.setText(htmlContent, true);
        return mimeMessage;
    }

    public Mail createMail(Campaign campaign, Audience audience) {
        Mail mail = new Mail();
        mail.setCampaignID(campaign.getId());
        mail.setCompanyID(campaign.getId());
        mail.setAudienceID(audience.getId());
        mail.setRecipientMail(audience.getEmail());
        mail.setSubject(campaign.getSubject());
        mail.setSendDate(LocalDate.now());
        mail.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        mail.setCheckTimes(0);
        return mail;
    }

    //要 follow function 的單一職責
    public Mail sendMimeMessage(MimeMessage mimeMessage, Mail mail, Audience audience) throws RuntimeException {
        try {
            mailSender.send(mimeMessage);
            String messageId = mimeMessage.getMessageID();
            //Split mimeMessageId to fit the payload of MailGun
            messageId = messageId.substring(1, messageId.length() - 1);
            log.info(messageId);
            mail.setMimeID(messageId);
            mail.setStatus(DeliveryStatus.RECEIVE.name());
            //update mail count when email been sent successfully.
            audienceService.updateMailCount(audience.getAudienceUUID());
            log.info("update mail count successfully!");
        } catch (MailException e) {
            handleMailException(e, mail, audience);
        } catch (MessagingException e) {
            log.error("error on creating mail base on mime message : " + e.getMessage());
            throw new RuntimeException(e);
        }
        return mail;
    }


    public void updateRedisMailFromSpringBoot(Mail mail) {
        RedisMail redisMail = new RedisMail();
        redisMail.setMimeID(mail.getMimeID());
        redisMail.setCampaignID(mail.getCampaignID());
        redisMail.setTimestamp(mail.getTimestamp());
        redisMail.setAudienceID(mail.getAudienceID());
        redisService.updateMailFromSpringboot(redisMail);
    }

    private void handleMailException(MailException e, Mail mail, Audience audience) {
        Long audienceId = audience.getId();
        switch (getMailExceptionType(e)) {
            case AUTHENTICATION:
                log.error("Mail Error: Error on sending email due to authentication issue: " + e.getMessage());
                break;
            case PARSE:
                log.error("Mail Error: Error on parsing email content: " + e.getMessage());
                break;
            case PREPARATION:
                log.error("Mail Error: Error on sending email to audience with id : " + audienceId + ": " + e.getMessage());
                break;
            default:
                log.error("Mail Error: Generic mail exception: " + e.getMessage());
        }
        mail.setStatus(DeliveryStatus.FAILED.name());
    }

    private MailExceptionType getMailExceptionType(MailException e) {
        if (e instanceof MailAuthenticationException) {
            return MailExceptionType.AUTHENTICATION;
        } else if (e instanceof MailParseException) {
            return MailExceptionType.PARSE;
        } else if (e instanceof MailPreparationException) {
            return MailExceptionType.PREPARATION;
        } else if (e instanceof MailSendException) {
            return MailExceptionType.SEND;
        } else {
            return null;
        }
    }

    private enum MailExceptionType {
        AUTHENTICATION,
        PARSE,
        PREPARATION,
        SEND
    }
}
