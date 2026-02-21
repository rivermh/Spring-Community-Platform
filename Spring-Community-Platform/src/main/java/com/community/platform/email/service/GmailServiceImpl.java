package com.community.platform.email.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
@Profile("!prod") // 로컬 환경(prod가 아닐 때)에서만 이 코드가 작동함!
public class GmailServiceImpl implements MailService {

	private final JavaMailSender mailSender;

    // Railway에 설정한 APP_BASE_URL 값
    // 로컬에서는 설정이 없으므로 기본값(localhost)
    @Value("${app.base.url:http://localhost:8080}")
    private String baseUrl;

    // 1. 회원가입 인증 메일
    public void sendVerificationMail(String to, String token) {
        String link = baseUrl + "/verify-email?token=" + token;
        String subject = "[회원가입] 이메일 인증 안내";
        String text = "안녕하세요. 아래 링크를 클릭하여 회원가입 인증을 완료해 주세요:\n" + link;
        
        sendMail(to, subject, text);
    }

    // 2. 비밀번호 재설정 메일 
    public void sendPasswordResetMail(String to, String token) {
        String link = baseUrl + "/reset-password?token=" + token;
        String subject = "[비밀번호 재설정] 인증 링크 안내";
        String text = "안녕하세요. 비밀번호를 재설정하시려면 아래 링크를 클릭해 주세요:\n" + link;
        
        sendMail(to, subject, text);
    }

    // 3. 실제 메일을 발송하는 공통 로직 
    private void sendMail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        
        mailSender.send(message);
    }
}
