package nl.fontys.data.services.interfaces;

public interface IMailService {
    void sendMail(final String recipient, final String subject, final String message);
}
