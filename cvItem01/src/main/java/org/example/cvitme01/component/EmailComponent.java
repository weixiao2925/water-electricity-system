package org.example.cvitme01.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailComponent {
    private final JavaMailSender mailSender;
    private final String fromAddress;

    public EmailComponent(JavaMailSender mailSender, @Value("${spring.mail.username}") String fromAddress){
        this.mailSender=mailSender;
        this.fromAddress=fromAddress;
    }

    public void senEmail(String to,String subject,String text){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
