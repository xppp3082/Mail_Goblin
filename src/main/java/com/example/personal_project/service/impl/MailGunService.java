package com.example.personal_project.service.impl;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;


@Slf4j
@Service
public class MailGunService {
    private static final String TEMPLATE_NAME = "registration";
    private static final String SPRING_LOGO_IMAGE = "templates/images/logo.png";
    private static final String PNG_MIME = "image/png";
    private static final String MAIL_SUBJECT = "Registration Confirmation";
    private final Environment environment;
    private final JavaMailSender mailSender;
    private final TemplateEngine htmlTemplateEngine;

    @Value("${mailgun.domain.name}")
    private String mailgunDomain;

    @Value("${mailgun.api.key}")
    private String mailgunAPIKey;

    public MailGunService(Environment environment, JavaMailSender mailSender, TemplateEngine htmlTemplateEngine) {
        this.environment = environment;
        this.mailSender = mailSender;
        this.htmlTemplateEngine = htmlTemplateEngine;
    }

    public MimeMessageHelper generateEmailContent() throws MessagingException, UnsupportedEncodingException {
        String confirmationUrl = "https://traviss.beauty/index.html?category=all";
        String mailFrom = environment.getProperty("spring.mail.properties.mail.smtp.from");
        String mailFromName = environment.getProperty("mail.from.name", "Identity");
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper email;
        email = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        email.setTo("xppp3081@gmail.com");
        email.setSubject(MAIL_SUBJECT);
        email.setFrom(new InternetAddress(mailFrom, mailFromName));

        Context ctx = new Context(LocaleContextHolder.getLocale());
        ctx.setVariable("email", "xppp3081@gmail.com");
        ctx.setVariable("name", "test user");
        ctx.setVariable("springLogo", SPRING_LOGO_IMAGE);
        ctx.setVariable("url", confirmationUrl);
        ctx.setVariable("url2","http://3.24.104.209/api/1.0/track/click?UID=550e8400-e29b-41d4-a716-446655440000&CID=19ef56c6-8749-34f2-6a0a-22e1eb43a244&cusWeb=https://traviss.beauty/index.html?category=all");
        ctx.setVariable("endpoint","http://3.24.104.209/api/1.0/track/open?UID=550e8400-e29b-41d4-a716-446655440000&CID=19ef56c6-8749-34f2-6a0a-22e1eb43a244");
        String htmlContent = this.htmlTemplateEngine.process(TEMPLATE_NAME, ctx);
        email.setText(htmlContent, true);
        ClassPathResource clr = new ClassPathResource(SPRING_LOGO_IMAGE);
        email.addInline("springLogo", clr, PNG_MIME);
        return email;
    }


    public String sendSimpleMessageViaMailgun2() throws MessagingException, UnsupportedEncodingException {
        MimeMessageHelper email = generateEmailContent();
        String API_KEY = mailgunAPIKey;
        String DOMAIN_NAME = mailgunDomain;
        HttpResponse<JsonNode> request =  Unirest.post("https://api.mailgun.net/v3/" + DOMAIN_NAME + "/messages.mime")
                .basicAuth("api", API_KEY)
                    .queryString("from", "Excited User <USER@YOURDOMAIN.COM>")
                .queryString("to", "xppp3081@gmail.com")
                .queryString("subject", "hello")
                .queryString("text", email.getMimeMessage())
                .asJson();
        log.info(request.getBody().toString());
        return request.getBody().toString();
    }

}
