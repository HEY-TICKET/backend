package com.heyticket.backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMessage.RecipientType;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender emailSender;

    public String sendSimpleMessage(String to) throws Exception {
        String key = createKey();// 랜덤 인증번호 생성
        MimeMessage message = createMessage(key, to); // 메일 발송
        try {// 예외처리
            emailSender.send(message);
        } catch (MailException es) {
            es.printStackTrace();
            throw new IllegalArgumentException();
        }

        return key; // 메일로 보냈던 인증 코드를 서버로 반환
    }

    public MimeMessage createMessage(String key, String to) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = emailSender.createMimeMessage();
        message.addRecipients(RecipientType.TO, to);
        message.setSubject("[헤이티켓] 회원 가입 인증 메일입니다.");

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<div style='margin:100px;'>");
        stringBuilder.append("<h1 align='center'> 안녕하세요</h1>");
        stringBuilder.append("<br>");
        stringBuilder.append("<div align='center' width='400px' style='border:1px solid black; font-family:verdana';>");
        stringBuilder.append("<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>");
        stringBuilder.append("<div style='font-size:130%'>");
        stringBuilder.append("CODE : <strong>");
        stringBuilder.append(key).append("</strong><div><br/> ");
        stringBuilder.append("</div>");
        message.setText(stringBuilder.toString(), "utf-8", "html");
        message.setFrom(new InternetAddress("heyticket@gmail.com", "헤이티켓"));

        return message;
    }

    private String createKey() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i = 0; i < 8; i++) { // 인증코드 8자리
            int index = rnd.nextInt(3); // 0~2 까지 랜덤, rnd 값에 따라서 아래 switch 문이 실행됨

            switch (index) {
                case 0 -> key.append((char) (rnd.nextInt(26) + 97)); // a~z (ex. 1+97=98 => (char)98 = 'b')
                case 1 -> key.append((char) (rnd.nextInt(26) + 65)); // A~Z
                case 2 -> key.append((rnd.nextInt(10))); // 0~9
            }
        }

        return key.toString();
    }
}
