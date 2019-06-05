package nl.fontys.data.services;

import nl.fontys.data.services.interfaces.IMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService implements IMailService {
    private JavaMailSender mailSender;

    @Autowired
    public MailService(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }

    @Override
    public void sendMail(String recipient, String subject, String message) {

    }
}
