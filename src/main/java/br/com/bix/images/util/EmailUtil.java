package br.com.bix.images.util;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailUtil {

    private final JavaMailSender emailSender;

    public void sendSimpleMessage(String to, String subject, String text) {
        final SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@bix.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }
}
