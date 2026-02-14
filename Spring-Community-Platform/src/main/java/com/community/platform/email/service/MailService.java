package com.community.platform.email.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {
 
    private final JavaMailSender mailSender;
    
    // 1. 회원가입 인증 메일
    public void sendVerificationMail(String to, String token) {
        String link = "http://localhost:8080/verify-email?token=" + token;
        String subject = "[회원가입] 이메일 인증 안내";
        String text = "안녕하세요. 아래 링크를 클릭하여 회원가입 인증을 완료해 주세요:\n" + link;
        
        sendMail(to, subject, text);
    }

    // 2. 비밀번호 재설정 메일 
    public void sendPasswordResetMail(String to, String token) {
        // 링크 주소가 /reset-password 
        String link = "http://localhost:8080/reset-password?token=" + token;
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