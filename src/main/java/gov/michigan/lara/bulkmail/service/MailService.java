package gov.michigan.lara.bulkmail.service;

import java.util.List;
import java.util.concurrent.Future;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    int size = 0;
    int count = 0;

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    @Async
    public Future<Integer> actualSend(Message message, Integer n, HttpServletRequest request)
            throws MessagingException {
        n++;
        String address = message.getAllRecipients()[0].toString();
        try {
            Transport.send(message);
            log.info("message sent to '" + address + "'");
        } catch (MessagingException e) {
            log.error("error sending message to '" + address + "' - " + e.getMessage());
            @SuppressWarnings(value = "unchecked")
            List<String> fails = (List<String>) request.getSession().getAttribute("fails");
            fails.add(address);
            request.getSession().setAttribute("fails", fails);
        } finally {
            
        }
        return new AsyncResult<Integer>(n);
    }
}