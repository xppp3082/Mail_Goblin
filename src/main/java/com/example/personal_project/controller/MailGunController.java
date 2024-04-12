package com.example.personal_project.controller;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class MailGunController {
    private static final String TEMPLATE_NAME = "registration";
    private static final String SPRING_LOGO_IMAGE = "templates/images/spring.png";
    private static final String PNG_MIME = "image/png";
    private static final String MAIL_SUBJECT = "Registration Confirmation";
    private final Environment environment;
    private final JavaMailSender mailSender;
    private final TemplateEngine htmlTemplateEngine;

    public MailGunController(Environment environment, JavaMailSender mailSender, TemplateEngine htmlTemplateEngine) {
        this.environment = environment;
        this.mailSender = mailSender;
        this.htmlTemplateEngine = htmlTemplateEngine;
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register()
            throws MessagingException, UnsupportedEncodingException {

        // TODO save user in the database here

        String confirmationUrl = "https://traviss.beauty/index.html?category=all";
        String mailFrom = environment.getProperty("spring.mail.properties.mail.smtp.from");
        String mailFromName = environment.getProperty("mail.from.name", "Identity");
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper email;
        email = new MimeMessageHelper(mimeMessage, true, "UTF-8");
//        email.setTo(user.getEmail());
        email.setTo("xppp3081@gmail.com");
        email.setSubject(MAIL_SUBJECT);
        email.setFrom(new InternetAddress(mailFrom, mailFromName));

        final Context ctx = new Context(LocaleContextHolder.getLocale());
//        ctx.setVariable("email", "xppp3082@gmail.com");
        ctx.setVariable("email", "xppp3081@gmail.com");
        ctx.setVariable("name", "test user");
        ctx.setVariable("springLogo", SPRING_LOGO_IMAGE);
        ctx.setVariable("url", confirmationUrl);
//        ctx.setVariable("url2","http://3.24.104.209/api/1.0/track/click?userId=123");
        ctx.setVariable("url2","http://3.24.104.209/api/1.0/track/click?UID=550e8400-e29b-41d4-a716-446655440000&CID=19ef56c6-8749-34f2-6a0a-22e1eb43a244&cusWeb=https://traviss.beauty/index.html?category=all");
        ctx.setVariable("endpoint","http://3.24.104.209/api/1.0/track/open?UID=550e8400-e29b-41d4-a716-446655440000&CID=19ef56c6-8749-34f2-6a0a-22e1eb43a244");
//        ctx.setVariable("endpoint","http://3.24.104.209/api/1.0/track/test.png?userId=123");
//        ctx.setVariable("testPNG","http://3.24.104.209/docker1.png");

        final String htmlContent = this.htmlTemplateEngine.process(TEMPLATE_NAME, ctx);
        email.setText(htmlContent, true);

        ClassPathResource clr = new ClassPathResource(SPRING_LOGO_IMAGE);
        email.addInline("springLogo", clr, PNG_MIME);
        mailSender.send(mimeMessage);

        Map<String, String> body = new HashMap<>();
        body.put("message", "User created successfully.");

        return new ResponseEntity<>(body, HttpStatus.OK);
    }
}
