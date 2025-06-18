package org.example.hansabal.domain.email.controller;

import jakarta.annotation.Resource;
import org.example.hansabal.domain.email.dto.request.MailRequest;
import org.example.hansabal.domain.email.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

@Controller
@RequestMapping("/payment")
public class MailController {

    @Resource(name = "mailService")
    private MailService mailService;

    @GetMapping("/send")
    public String send(@RequestBody MailRequest mail) {
        List<String> recipientList = new ArrayList<>();
        recipientList.add("imleo322@gmail.com");

        mail.setTitle("이메일 보내기 테스트");
        mail.setContent("이메일 내용");
        mail.setRecipientList(recipientList);


        mailService.sendSimpleEmail(mail);
        return "이메일 보내기 완료";
    }
}
