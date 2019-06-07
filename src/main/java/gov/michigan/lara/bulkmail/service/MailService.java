package gov.michigan.lara.bulkmail.service;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import gov.michigan.lara.bulkmail.domain.EmailObject;

@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    public void sendEmail(List<EmailObject> emails, String from, String subject, String body) throws InterruptedException, ExecutionException {
        log.info("Invoking an asynchronous method. " + Thread.currentThread().getName());

        Properties props = new Properties();
        props.put("mail.smtp.host", "coreosmtp.state.mi.us");
        props.put("mail.debug", "false");
        props.put("mail.smtp.auth", "false");
        props.put("mail.smtp.ssl.trust", "coreosmtp.state.mi.us");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props);

        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(from));
            message.setSubject(subject);
            message.setContent(body, "text/html");
        } catch (MessagingException e) {
            log.error("error creating message: " + e.getMessage());
        }

        Integer count = 0;
        Future<Integer> future = null;
        for (EmailObject email : emails) {
            try {
                Thread.sleep(3000);
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email.getEmail()));
                future = actualSend(message, count);
                count = future.get();
            } catch (MessagingException ex) {
                count++;
                log.error("error sending message to '" + email.getEmail() + "' - " + ex.getMessage());
            }
            Double percent = new Double(count) / new Double(emails.size()) * 100;
            log.info("Total addresses processed: " + count + "/" + emails.size() + " = " + percent + "%");
        }

        if (count == emails.size()) {
            log.info("process completed");
        }
    }

    @Async
    public Future<Integer> actualSend(Message message, Integer n) throws MessagingException {
        n++;
        try {
            Thread.sleep(2000);
            Transport.send(message);
            log.info("message sent to '" + message.getAllRecipients()[0] + "'");
        } catch (InterruptedException e) {
            log.error("process interrupted '" + message.getAllRecipients()[0] + "' - " + e.getMessage());
        } catch (MessagingException e) {
            log.error("error sending message to '" + message.getAllRecipients()[0] + "' - " + e.getMessage());
        }
        return new AsyncResult<Integer>(n);
    }
}