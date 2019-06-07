package gov.michigan.lara.bulkmail.service;

import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import gov.michigan.lara.bulkmail.domain.EmailObject;

@Service
public class MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    public void sendEmail(List<EmailObject> emails, String from, String subject, String body) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "coreosmtp.state.mi.us");
        props.put("mail.debug", "false");
        props.put("mail.smtp.auth", "false");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props);

        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(from));
            message.setSubject(subject);
            message.setContent(body, "text/html");
        } catch (MessagingException e) {
            logger.error("error creating message: " + e.getMessage());
        }

        for (EmailObject email : emails) {
            try {
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email.getEmail()));
                Transport.send(message);
                logger.info("email sent to " + email.getEmail());
            } catch (MessagingException ex) {
                logger.error("error sending message to '" + email.getEmail() + "' - " + ex.getMessage());
            }
        }

    }
}