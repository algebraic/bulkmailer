package gov.michigan.lara.bulkmail.controller;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import gov.michigan.lara.bulkmail.domain.EmailObject;
import gov.michigan.lara.bulkmail.service.MailService;
import gov.michigan.lara.bulkmail.util.CsvUtils;

@Controller
@ControllerAdvice
public class MailController {

    int size = 0;
    int count = 0;
    Double percent = 0.0;

    private static final Logger log = LoggerFactory.getLogger(MailController.class);
    public MailService mailService = new MailService();

    @ModelAttribute
    @GetMapping(value = "/")
    public ModelAndView test() {
        ModelAndView mav = new ModelAndView("index");
        this.size = 0;
        this.count = 0;
        mav.addObject("count", count);
        mav.addObject("size", size);
        return mav;
    }

    @ResponseBody
    @PostMapping(value = "/check")
    public int uploadMultipart(@RequestParam("file") MultipartFile file) throws IOException {
        log.info("loading data from " + file.getOriginalFilename());
        List<EmailObject> emails = new ArrayList<EmailObject>();
        emails.addAll(CsvUtils.read(EmailObject.class, file.getInputStream()));
        log.info("read " + emails.size() + " email addresses from file");
        for (EmailObject e : emails) {
            log.info(e.getEmail());
        }
        this.size = emails.size();
        return this.size;
    }

    @ResponseBody
    @PostMapping(value = "/load")
    public void load(@RequestParam("file") MultipartFile file, @RequestParam("from") String from,
            @RequestParam("subject") String subject, @RequestParam("body") String body, HttpServletRequest request)
            throws IOException, InterruptedException, ExecutionException {

        List<EmailObject> emails = new ArrayList<EmailObject>();
        emails.addAll(CsvUtils.read(EmailObject.class, file.getInputStream()));
        log.info("sending emails for " + emails.size() + " addresses");

        this.size = emails.size();

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

        List<String> fails = new ArrayList<String>();
        request.getSession().setAttribute("fails", fails);

        Future<Integer> future = null;
        for (EmailObject email : emails) {
            try {
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email.getEmail()));
                future = mailService.actualSend(message, this.count, request);
                this.count = future.get();
            } catch (MessagingException ex) {
                this.count++;
                log.error("error sending message to '" + email.getEmail() + "' - " + ex.getMessage());
            }
            Double percent = new Double(this.count) / new Double(this.size) * 100;
            this.percent = percent;
            log.info("Total addresses processed: " + this.count + "/" + this.size + " = " + percent + "%");
        }

        if (count == this.size) {
            log.info("mail process completed");
            this.count = 0;
            this.size = 0;
            this.percent = 0.0;
        }
    }

    @ResponseBody
    @GetMapping(value = "/getProgress")
    public String getProgress() {
        DecimalFormat dec = new DecimalFormat("#0");
        Double percent = new Double(this.count) / new Double(this.size) * 100;
        return dec.format(percent);
    }

    @ResponseBody
    @GetMapping(value = "/getFails")
    public List<String> getProgress(HttpServletRequest request) {
        @SuppressWarnings(value = "unchecked")
        List<String> fails = (List<String>) request.getSession().getAttribute("fails");
        return fails;
    }

}
