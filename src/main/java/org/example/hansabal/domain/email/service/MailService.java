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

    public void sendSimpleEmail(MailRequest mail) {
        SimpleMailMessage message = new SimpleMailMessage();
        List<String> recipientList = new ArrayList<>();
        recipientList.add("imleo322@gmail.com");
        mail.setRecipientList(recipientList);

        message.setSubject(mail.getTitle()); //메일제목
        //리스트를 배열로 만들건데 문자열 배열로 만들어주세요. 리스트 사이즈 만큼 변환할 것이다. 라는 뜻이다.
        message.setTo(mail.getRecipientList().toArray(new String[mail.getRecipientList().size()])); //보낼사람들
        message.setText(mail.getContent()); //메일 내용

        javaMailSender.send(message);
    }
}
