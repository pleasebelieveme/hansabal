package org.example.hansabal.domain.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import static io.lettuce.core.pubsub.PubSubOutput.Type.message;

/*
이메일의 제목, 본문, 수신자, 첨부파일 등 각종 속성을 간단한 메소드로 편리하게 설정
HTML 본문이나 여러 수신자, 첨부파일, 인라인 이미지와 같은 복잡한 이메일 작성 가능
문자 인코딩(UTF-8 등) 설정 간편
*/
@Slf4j
@Service("mailService")
@RequiredArgsConstructor
public class MailService {

    //Math.random() : 0.0 이상 1.0 미만의 난수(무작위 실수)를 생성해줍니다.
    //0 ~ 89999에 100000을 더하면, 100000 ~ 189999 범위의 값이 됩니다.
    //(int) Math.random() * (최댓값-최소값+1) + 최소값
    private static int number;

    public void createNumber() {
        number = (int) (Math.random() * (90000)) + 100000;
    }

    private final JavaMailSender javaMailSender; // 이메일 송수신 기능을 지원하는 주요 인터페이스
    private final SpringTemplateEngine templateEngine;

    //인증번호 이메일
    public void numberVerificationEmail(String email) {
        try {
            createNumber();
            Context context = new Context();
            context.setVariable("number", number);
            String html = templateEngine.process("mail_verification", context);
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("이메일 인증");
            helper.setText(html, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            log.error("이메일 발송 실패. 대상: {}, 원인: {}", email, e.getMessage(), e);
        }
    }

    //구매완료 이메일
    public void purchaseCompletedEmail(String name, String email) {
        try {
            Context context = new Context();
            context.setVariable("name", name);
            String html = templateEngine.process("paymentMail", context);
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8"); //MimeMessage 객체의 설정을 쉽게 도와주는 보조 클래스
            helper.setTo(email);
            helper.setSubject("구매 완료 안내");
            helper.setText(html, true);
            javaMailSender.send(message);
        } catch (Exception e) {
            log.error("이메일 발송 실패. 대상: {}, 원인: {}", email, e.getMessage(), e);
        }
    }

    //회원가입완료 이메일 //todo 일단 테스트 해보고 수정 ㄱㄱ
    public void signUpCompletedEmail(String name, String email) {
        try {
            Context context = new Context();
            context.setVariable("name", name);
            String html = templateEngine.process("signUpMail", context);
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8"); //MimeMessage 객체의 설정을 쉽게 도와주는 보조 클래스
            helper.setTo(email);
            helper.setSubject("회원 가입완료 안내");
            helper.setText(html, true);
            javaMailSender.send(message);
        } catch (Exception e) {
            log.error("이메일 발송 실패. 대상: , 원인: {}", e.getMessage(), e);
        }
    }
}