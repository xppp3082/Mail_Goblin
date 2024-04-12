package com.example.personal_project.service.impl;

import com.example.personal_project.model.Audience;
import com.example.personal_project.model.EmailCampaign;
import com.example.personal_project.model.Mail;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
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

    public MailServerService(Environment environment, JavaMailSender mailSender, TemplateEngine htmlTemplateEngine) {
        this.environment = environment;
        this.mailSender = mailSender;
        this.htmlTemplateEngine = htmlTemplateEngine;
    }

    public String sendRegisterMail()
            throws MessagingException, UnsupportedEncodingException {
        try {
            String confirmationUrl = "https://traviss.beauty/index.html?category=all";
            String mailFrom = environment.getProperty("spring.mail.properties.mail.smtp.from");
            String mailFromName = environment.getProperty("mail.from.name", "Identity");
            final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
            final MimeMessageHelper email;
            email = new MimeMessageHelper(mimeMessage, true, "UTF-8");
//        email.setTo(user.getEmail());
            email.setTo("xppp3081@gmail.com");
//            email.setTo("leslie20100430@gmail.com");
            email.setSubject(MAIL_SUBJECT);
            email.setFrom(new InternetAddress(mailFrom, mailFromName));

            final Context ctx = new Context(LocaleContextHolder.getLocale());
            ctx.setVariable("email", "xppp3081@gmail.com");
            ctx.setVariable("name", "test user");
            ctx.setVariable("springLogo", SPRING_LOGO_IMAGE);
            ctx.setVariable("url", confirmationUrl);
//            ctx.setVariable("endpoint","https://traviss.beauty/api/1.0/user/test.png?category=XX");
            ctx.setVariable("url2","http://3.24.104.209/api/1.0/track/click?UID=550e8400-e29b-41d4-a716-446655440000&CID=19ef56c6-8749-34f2-6a0a-22e1eb43a244&cusWeb=https://traviss.beauty/index.html?category=all");
            ctx.setVariable("endpoint","http://3.24.104.209/api/1.0/track/open?UID=550e8400-e29b-41d4-a716-446655440000&CID=19ef56c6-8749-34f2-6a0a-22e1eb43a244");
//            ctx.setVariable("testPNG","http://3.24.104.209/docker1.png");
            final String htmlContent = this.htmlTemplateEngine.process(TEMPLATE_NAME, ctx);
            email.setText(htmlContent, true);

            ClassPathResource clr = new ClassPathResource(SPRING_LOGO_IMAGE);
            email.addInline("springLogo", clr, PNG_MIME);
            mailSender.send(mimeMessage);
            return "User created successfully";
        } catch (Exception e) {
            return "User created failed";
        }
    }
//    public String sendBatchMails(List<Mail>mails)
    public String sendBatchMails(EmailCampaign emailCampaign)
            throws MessagingException, UnsupportedEncodingException {
        try {
            for(Audience audience: emailCampaign.getAudiences()){
                String confirmationUrl = "https://traviss.beauty/index.html?category=all";
//                String openTrackUrl = String.format("\"http://3.24.104.209/api/1.0/track/open?UID=550e8400-e29b-41d4-a716-446655440000&CID=19ef56c6-8749-34f2-6a0a-22e1eb43a244\"");
                String openTrackUrl = String.format("http://3.24.104.209/api/1.0/track/open?UID=%S",audience.getAudienceUUID());
                String clickTrackUrl = String.format("http://3.24.104.209/api/1.0/track/click?UID=%S&cusWeb=https://traviss.beauty/index.html?category=all",audience.getAudienceUUID());
                log.info(openTrackUrl);
                log.info(clickTrackUrl);
                String mailFrom = environment.getProperty("spring.mail.properties.mail.smtp.from");
                String mailFromName = environment.getProperty("mail.from.name", "Identity");
                final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
                final MimeMessageHelper email;
                email = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                email.setTo("xppp3081@gmail.com");
                email.setSubject(emailCampaign.getCampaign().getSubject());
                email.setFrom(new InternetAddress(mailFrom, mailFromName));

                final Context ctx = new Context(LocaleContextHolder.getLocale());
                ctx.setVariable("email", audience.getEmail());
                ctx.setVariable("name",audience.getName());
                ctx.setVariable("springLogo", SPRING_LOGO_IMAGE);
                ctx.setVariable("url", confirmationUrl);
                ctx.setVariable("url2",clickTrackUrl);
                ctx.setVariable("endpoint",openTrackUrl);
                final String htmlContent = this.htmlTemplateEngine.process(TEMPLATE_NAME, ctx);
                email.setText(htmlContent, true);

                ClassPathResource clr = new ClassPathResource(SPRING_LOGO_IMAGE);
                email.addInline("springLogo", clr, PNG_MIME);
                mailSender.send(mimeMessage);
            }
            return "User created successfully";
        } catch (Exception e) {
            return "User created failed";
        }
    }
}
