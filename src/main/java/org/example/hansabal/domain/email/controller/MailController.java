//package org.example.hansabal.domain.email.controller;
//
//import jakarta.annotation.Resource;
//import lombok.RequiredArgsConstructor;
//import org.example.hansabal.domain.email.service.MailService;
//import org.example.hansabal.domain.payment.dto.request.RequestPayDto;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.*;
//
//@Controller
//@RequiredArgsConstructor
//public class MailController {
//
//    @Resource(name = "mailService")
//    private MailService mailService;
//
//    @PostMapping("/send-email")
//    public String send() {
//        mailService.sendSimpleEmail();
//        return "PaymentMail";
//    }
//}
// 일단 테스트 해보고 구현확인후 추후에 삭제하겠습니다.
