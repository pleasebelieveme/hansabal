package org.example.hansabal.domain.email.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class MailRequest {

    private String title;  //제목
    private String content; //내용
    private List<String> recipientList;  //받는사람
}
