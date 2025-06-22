package org.example.hansabal.domain.email.controller;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.example.hansabal.domain.email.service.MailService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class MailController {

    @Resource(name = "mailService")
    private MailService mailService;

    @PostMapping("/send-email")
    public String send() {
        mailService.signUpEmail();
        return "PaymentMail";
    }
}

