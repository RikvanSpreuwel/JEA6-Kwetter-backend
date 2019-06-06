package nl.fontys.data.services;

import nl.fontys.data.services.interfaces.IMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.MimeMessageHelper;
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
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(message);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
