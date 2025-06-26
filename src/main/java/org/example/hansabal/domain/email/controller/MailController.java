package org.example.hansabal.domain.email.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hansabal.domain.email.dto.request.EmailAuthCodeRequest;
import org.example.hansabal.domain.email.dto.request.EmailAuthCodeVerifyRequest;
import org.example.hansabal.domain.email.service.MailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MailController {

    @Resource(name = "mailService")
    private MailService mailService;

//    @PostMapping("/send-email")
//    public String send() {
//        String name = "이귀현";
//        String email = "imleo322@hanmail.net";
//        mailService.signUpCompletedEmail(name, email);
//        return "PaymentMail";
//    }

    @PostMapping("/api/auth/send-code")
    public ResponseEntity<Void> sendCode(@RequestBody @Valid EmailAuthCodeRequest request) {
        mailService.sendVerificationCode(request.email());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/auth/verify-code")
    public ResponseEntity<Void> verifyCode(@RequestBody @Valid EmailAuthCodeVerifyRequest request) {
        mailService.verifyCode(request.email(), request.code());
        return ResponseEntity.ok().build(); // 성공 시 응답 바디 없이 200 OK
    }

}

