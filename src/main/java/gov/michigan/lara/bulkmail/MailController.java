package gov.michigan.lara.bulkmail;

import java.io.IOException;
import java.util.ArrayList;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class MailController {

    private static final Logger logger = LoggerFactory.getLogger(MailController.class);

    @ResponseBody
    @PostMapping(value = "/check")
    public int uploadMultipart(@RequestParam("file") MultipartFile file) throws IOException {
        logger.info("loading data from " + file.getOriginalFilename());
        List<EmailObject> emails = new ArrayList<EmailObject>();
        emails.addAll(CsvUtils.read(EmailObject.class, file.getInputStream()));
        logger.info("read " + emails.size() + " email addresses from file");
        for (EmailObject e : emails) {
            logger.info(e.getEmail());
        }
        return emails.size();
    }

    @ResponseBody
    @PostMapping(value = "/load")
    public void load(@RequestParam("file") MultipartFile file, @RequestParam("from") String from,
            @RequestParam("subject") String subject, @RequestParam("body") String body) throws IOException {

        List<EmailObject> emails = new ArrayList<EmailObject>();
        emails.addAll(CsvUtils.read(EmailObject.class, file.getInputStream()));

        logger.info("sending emails for " + emails.size() + " addresses");
        sendEmail(emails, from, subject, body);
    }

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
