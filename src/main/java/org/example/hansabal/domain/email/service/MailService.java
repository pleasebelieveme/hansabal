package org.example.hansabal.domain.email.service;

import jakarta.mail.internet.MimeMessage;
import org.example.hansabal.domain.email.dto.request.MailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
/*
이메일의 제목, 본문, 수신자, 첨부파일 등 각종 속성을 간단한 메소드로 편리하게 설정
HTML 본문이나 여러 수신자, 첨부파일, 인라인 이미지와 같은 복잡한 이메일 작성 가능
문자 인코딩(UTF-8 등) 설정 간편
*/
@Service("mailService")
public class MailService {

    @Autowired
    private JavaMailSender javaMailSender; // 이메일 송수신 기능을 지원하는 주요 인터페이스

    public void sendSimpleEmail(MailRequest dto) {

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8"); //MimeMessage 객체의 설정을 쉽게 도와주는 보조 클래스
            helper.setSubject(dto.getTitle());
            helper.setTo(dto.getRecipient());
            helper.setText(dto.getContent(), true);
            javaMailSender.send(message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}