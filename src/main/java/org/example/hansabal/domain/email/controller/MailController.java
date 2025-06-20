package org.example.hansabal.domain.email.controller;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.example.hansabal.domain.email.dto.request.MailRequest;
import org.example.hansabal.domain.email.service.MailService;
import org.example.hansabal.domain.payment.dto.request.RequestPayDto;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

@Controller
@RequiredArgsConstructor
public class MailController {

    @Resource(name = "mailService")
    private MailService mailService;

    @PostMapping("/send-email")
    public String send(@RequestBody MailRequest request) {
        mailService.sendSimpleEmail(request);
        return "PaymentMail";
    }
    // 이부분은 한번더 수정해야 되는 부분이 있어서 변경될 예정입니다
}
