package gov.michigan.lara.bulkmail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @ResponseBody
    @PostMapping(value = "/load", consumes = "multipart/form-data")
    public int uploadMultipart(@RequestParam("file") MultipartFile file) throws IOException {
        System.out.println("### hi");
        logger.info("loading data from " + file.getOriginalFilename());
        List<EmailObject> emails = new ArrayList<EmailObject>();
        emails.addAll(CsvUtils.read(EmailObject.class, file.getInputStream()));
        logger.info("read " + emails.size() + " products from file");

        for (EmailObject e : emails) {
            System.out.println(e.getEmail());
        }

        return emails.size();
    }

    public void sendEmail(List<EmailObject> emails) {
        // iterate over email list and send stuff
        
        String subject = "Test Email";
        String message = "Test Email Message";
    }

}	
