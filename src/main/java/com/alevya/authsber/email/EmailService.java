package com.alevya.authsber.registration;

import com.alevya.authsber.email.EmailSender;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService implements EmailSender {
    private static final Log log = LogFactory.getLog(EmailService.class);

    private JavaMailSender mailSender;
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
//    @Async
    public void send(String to, String email) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject("Email confirmation");
            helper.setFrom("hello@hello.ru");
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("failed to send email");
            throw new IllegalStateException("failed to send email");
        }
    }
}
