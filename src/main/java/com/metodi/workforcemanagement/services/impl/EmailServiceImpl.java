package com.metodi.workforcemanagement.services.impl;

import com.metodi.workforcemanagement.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@PropertySource("classpath:email-values.properties")
public class EmailServiceImpl implements EmailService {
    @Value("${email.template.subject}")
    private  String subject;
    private final JavaMailSender emailSender;

    @Autowired
    public EmailServiceImpl(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    public void sendEmails(String[] to, String requester, String message) {
        try {
            SimpleMailMessage emailMessage = new SimpleMailMessage();
            emailMessage.setTo(to);
            emailMessage.setSubject(String.format(this.subject, requester));
            emailMessage.setText(message);

            emailSender.send(emailMessage);
        } catch (MailException exception) {
            throw exception;
        }
    }
}
