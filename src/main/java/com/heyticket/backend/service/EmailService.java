package com.heyticket.backend.service;

import com.heyticket.backend.service.dto.request.EmailSendRequest;
import com.heyticket.backend.service.dto.request.VerificationRequest;
import com.heyticket.backend.service.enums.VerificationType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMessage.RecipientType;
import java.io.UnsupportedEncodingException;
import java.util.NoSuchElementException;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;

    private final CacheService cacheService;

    public String sendSimpleMessage(EmailSendRequest request) {
        String email = request.getEmail();
        String code = createCode();
        MimeMessage message;
        if (request.getVerificationType() == VerificationType.SIGN_UP) {
            message = createSignUpMessage(email, code);
        } else {
            message = createPasswordFindMessage(email, code);
        }
        try {
            emailSender.send(message);
        } catch (MailException es) {
            es.printStackTrace();
            throw new IllegalArgumentException();
        }
        cacheService.putCode(email, code);

        return email;
    }

    public boolean verifyCode(VerificationRequest request) {
        String email = request.getEmail();
        String code = request.getCode();

        String savedCode = cacheService.getCodeIfPresent(email);

        if (savedCode == null) {
            throw new NoSuchElementException("해당 메일의 인증 내역이 없습니다.");
        }
        return savedCode.equals(code);
    }

    public String expireCode(String email) {
        cacheService.invalidateCode(email);
        return email;
    }

    private MimeMessage createSignUpMessage(String email, String code) {
        MimeMessage message = emailSender.createMimeMessage();
        try {
            message.addRecipients(RecipientType.TO, email);
            message.setSubject("[헤이티켓] 회원 가입 인증 메일입니다.");

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<div style='margin:100px;'>");
            stringBuilder.append("<h1 align='center'> 안녕하세요</h1>");
            stringBuilder.append("<br>");
            stringBuilder.append("<div align='center' width='400px' style='border:1px solid black; font-family:verdana';>");
            stringBuilder.append("<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>");
            stringBuilder.append("<div style='font-size:130%'>");
            stringBuilder.append("CODE : <strong>");
            stringBuilder.append(code).append("</strong><div><br/> ");
            stringBuilder.append("</div>");
            message.setText(stringBuilder.toString(), "utf-8", "html");
            message.setFrom(new InternetAddress("heyticket@gmail.com", "헤이티켓"));
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return message;
    }

    private MimeMessage createPasswordFindMessage(String email, String code) {
        MimeMessage message = emailSender.createMimeMessage();
        try {
            message.addRecipients(RecipientType.TO, email);
            message.setSubject("[헤이티켓] 비밀번호 찾기 인증 메일입니다.");

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<div style='margin:100px;'>");
            stringBuilder.append("<h1 align='center'> 안녕하세요</h1>");
            stringBuilder.append("<br>");
            stringBuilder.append("<div align='center' width='400px' style='border:1px solid black; font-family:verdana';>");
            stringBuilder.append("<h3 style='color:blue;'>비밀번호 찾기 인증 코드입니다.</h3>");
            stringBuilder.append("<div style='font-size:130%'>");
            stringBuilder.append("CODE : <strong>");
            stringBuilder.append(code).append("</strong><div><br/> ");
            stringBuilder.append("</div>");
            message.setText(stringBuilder.toString(), "utf-8", "html");
            message.setFrom(new InternetAddress("heyticket@gmail.com", "헤이티켓"));
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return message;
    }

    private String createCode() {
        StringBuilder code = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 8; i++) { // 인증코드 8자리
            int index = random.nextInt(3); // 0~2 까지 랜덤, random 값에 따라서 아래 switch 문이 실행됨
            switch (index) {
                case 0 -> code.append((char) (random.nextInt(26) + 97)); // a~z (ex. 1+97=98 => (char)98 = 'b')
                case 1 -> code.append((char) (random.nextInt(26) + 65)); // A~Z
                case 2 -> code.append((random.nextInt(10))); // 0~9
            }
        }

        return code.toString();
    }
}
