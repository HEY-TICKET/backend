package com.heyticket.backend.service;

import com.heyticket.backend.exception.InternalCode;
import com.heyticket.backend.exception.SmtpFailureException;
import com.heyticket.backend.module.util.VerificationCodeGenerator;
import com.heyticket.backend.service.dto.VerificationCode;
import com.heyticket.backend.service.dto.request.EmailSendRequest;
import com.heyticket.backend.service.enums.VerificationType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMessage.RecipientType;
import java.io.UnsupportedEncodingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;

@Slf4j
public class EmailService implements IEmailService {

    private final JavaMailSender mailSender;

    private final LocalCacheService localCacheService;

    public EmailService(JavaMailSender mailSender, LocalCacheService localCacheService) {
        this.mailSender = mailSender;
        this.localCacheService = localCacheService;
    }

    public String sendSimpleMessage(EmailSendRequest request) {
        String email = request.getEmail();
        String code = VerificationCodeGenerator.createCode();
        MimeMessage message;
        VerificationCode verificationCode;
        if (request.getVerificationType() == VerificationType.SIGN_UP) {
            message = createSignUpMessage(email, code);
            verificationCode = VerificationCode.of(code, System.currentTimeMillis() + 600000);
        } else {
            message = createPasswordFindMessage(email, code);
            verificationCode = VerificationCode.of(code, System.currentTimeMillis() + 180000);
        }

        try {
            mailSender.send(message);
        } catch (MailException es) {
            es.printStackTrace();
            throw new IllegalArgumentException();
        }

        localCacheService.putVerificationCode(email, verificationCode);

        return email;
    }

    private MimeMessage createSignUpMessage(String email, String code) {
        MimeMessage message = mailSender.createMimeMessage();
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
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Failure to send sign up mail", e);
            throw new SmtpFailureException("Failure to send sign up mail", InternalCode.SERVER_ERROR);
        }

        return message;
    }

    private MimeMessage createPasswordFindMessage(String email, String code) {
        MimeMessage message = mailSender.createMimeMessage();
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
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Fail to send password find mail", e);
            throw new SmtpFailureException("Fail to send password find mail", InternalCode.SERVER_ERROR);
        }

        return message;
    }
}
