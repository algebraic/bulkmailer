package gov.michigan.lara.bulkmail.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import gov.michigan.lara.bulkmail.domain.EmailObject;
import gov.michigan.lara.bulkmail.service.MailService;
import gov.michigan.lara.bulkmail.util.CsvUtils;

@Controller
public class MailController {

    private static final Logger log = LoggerFactory.getLogger(MailController.class);
    public MailService mailService = new MailService();

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
        return emails.size();
    }

    @ResponseBody
    @PostMapping(value = "/load")
    public void load(@RequestParam("file") MultipartFile file, @RequestParam("from") String from,
            @RequestParam("subject") String subject, @RequestParam("body") String body) throws IOException, InterruptedException, ExecutionException {

        List<EmailObject> emails = new ArrayList<EmailObject>();
        emails.addAll(CsvUtils.read(EmailObject.class, file.getInputStream()));
        log.info("sending emails for " + emails.size() + " addresses");
        mailService.sendEmail(emails, from, subject, body);
    }

}
