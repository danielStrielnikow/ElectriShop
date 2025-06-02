package pl.ecommerce.project.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String myEmail;

    @Value("${spring.mail.password}")
    private String password;


    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendContactEmail(String name, String email, String message) {
        SimpleMailMessage mail = new SimpleMailMessage();

        mail.setTo(myEmail);
        mail.setSubject("Nowa wiadomość kontaktowa od: " + name);
        mail.setText("Email " + email + "\n\nWiadomość:\n" + message);
        mailSender.send(mail);
    }
}
