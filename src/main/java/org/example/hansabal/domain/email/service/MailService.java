package org.example.hansabal.domain.email.service;

import org.example.hansabal.domain.email.dto.request.MailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

@Service("mailService")
public class MailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendSimpleEmail(MailRequest dto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(dto.getTitle()); //메일제목
        message.setText(dto.getContent()); //메일 내용
        message.setTo(dto.getRecipient());
        javaMailSender.send(message);
    }
}
//        List<String> recipientList = new ArrayList<>();
//        recipientList.add("imleo322@gmail.com");
//        mail.setRecipientList(recipientList);
//        message.setTo(mail.getRecipientList().toArray(new String[mail.getRecipientList().size()])); //보낼사람들
//  나중에 사용하려고 주석처리 하였습니다.