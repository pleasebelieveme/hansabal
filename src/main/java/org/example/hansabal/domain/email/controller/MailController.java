package org.example.hansabal.domain.email.controller;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.example.hansabal.domain.email.dto.request.MailRequest;
import org.example.hansabal.domain.email.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

@RestController
@RequestMapping("/send-email")
@RequiredArgsConstructor
public class MailController {

    @Resource(name = "mailService")
    private MailService mailService;

    @PostMapping
    public String send(@RequestBody MailRequest request) {
        mailService.sendSimpleEmail(request);
        return "이메일 보내기 성공";
    }
}
