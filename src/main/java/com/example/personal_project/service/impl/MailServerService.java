package com.example.personal_project.service.impl;

import com.example.personal_project.model.*;
import com.example.personal_project.model.status.DeliveryStatus;
import com.example.personal_project.service.AudienceService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
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
    private static final String PNG_MIME = "image/png";
    private static final String MAIL_SUBJECT = "Registration Confirmation";
    private final Environment environment;
    private final JavaMailSender mailSender;
    private final TemplateEngine htmlTemplateEngine;
    private final AudienceService audienceService;
    private final RedisService redisService;

    public MailServerService(Environment environment, JavaMailSender mailSender, TemplateEngine htmlTemplateEngine, AudienceService audienceService, RedisService redisService) {
        this.environment = environment;
        this.mailSender = mailSender;
        this.htmlTemplateEngine = htmlTemplateEngine;
        this.audienceService = audienceService;
        this.redisService = redisService;
    }


    public List<Mail> sendBatchMails(EmailCampaign emailCampaign)
            throws MessagingException, UnsupportedEncodingException {
        List<Mail> mails = new ArrayList<>();
        try {
            MailTemplate mailTemplate = emailCampaign.getMailTemplate();
            for (Audience audience : emailCampaign.getAudiences()) {
                String confirmationUrl = mailTemplate.getUrl();
                log.info(audience.getEmail());
                String openTrackUrl = String.format("https://mailgoblin.site/api/1.0/track/open?UID=%s&CID=%s&recipient=%s&subject=%s",
                        audience.getAudienceUUID(),
                        emailCampaign.getCampaign().getId(),
                        audience.getEmail(),
                        emailCampaign.getCampaign().getSubject());
                String clickTrackUrl = String.format("https://mailgoblin.site/api/1.0/track/click?UID=%s&CID=%s&cusWeb=%s&recipient=%s&subject=%s",
                        audience.getAudienceUUID(),
                        emailCampaign.getCampaign().getId(),
                        confirmationUrl,
                        audience.getEmail(),
                        emailCampaign.getCampaign().getSubject());
                log.info(openTrackUrl + clickTrackUrl);
                String mailFrom = environment.getProperty("spring.mail.properties.mail.smtp.from");
                String mailFromName = environment.getProperty("mail.from.name", "Identity");
                final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
                final MimeMessageHelper email;
                email = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                email.setTo(audience.getEmail());
                email.setSubject(emailCampaign.getCampaign().getSubject());
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

                Mail mail = new Mail();
                mail.setCampaignID(emailCampaign.getCampaign().getId());
                mail.setCompanyID(emailCampaign.getCampaign().getId());
                mail.setAudienceID(audience.getId());
                mail.setRecipientMail(audience.getEmail());
                mail.setSubject(emailCampaign.getCampaign().getSubject());
                mail.setSendDate(LocalDate.now());
                mail.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
                mail.setCheckTimes(0);
                try {
                    mailSender.send(mimeMessage);
                    String messageId = mimeMessage.getMessageID();
                    messageId = messageId.substring(1, messageId.length() - 1);
                    log.info(messageId);
                    mail.setMimeID(messageId);
                    mail.setStatus(DeliveryStatus.RECEIVE.name());
                    mails.add(mail);
                    //update mail count when email been sent successfully.
                    audienceService.updateMailCount(audience.getAudienceUUID());
                    log.info("update mail count successfully!");
                } catch (MailException e) {
                    Long audienceId = audience.getId();
                    switch (getMailExceptionType(e)) {
                        case AUTHENTICATION:
                            log.warn("Mail Error: Error on sending email due to authentication issue: " + e.getMessage());
                            break;
                        case PARSE:
                            log.warn("Mail Error: Error on parsing email content: " + e.getMessage());
                            break;
                        case PREPARATION:
                            log.warn("Mail Error: Error on sending email to audience with id : " + audienceId + ": " + e.getMessage());
                            break;
                        default:
                            log.warn("Mail Error: Generic mail exception: " + e.getMessage());
                    }
                    mail.setStatus(DeliveryStatus.FAILED.name());
                    mails.add(mail);
                }
                //insert mail data to redis
                try {
                    RedisMail redisMail = new RedisMail();
                    redisMail.setMimeID(mail.getMimeID());
                    redisMail.setCampaignID(mail.getCampaignID());
                    redisMail.setTimestamp(mail.getTimestamp());
                    redisMail.setAudienceID(mail.getAudienceID());
                    redisService.updateMailFromSpringboot(redisMail);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
//            return mails;
        } catch (Exception e) {
            log.error("Error sending batch mails", e);
//            return null;
        }
        return mails;
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
