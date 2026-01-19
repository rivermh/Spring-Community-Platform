package com.example.demo.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    
    public void sendVerificationMail(String to, String token) {
    	String link = "http://localhost:8080/verify-email?token=" + token;
    	
    	SimpleMailMessage message = new SimpleMailMessage();
    	message.setTo(to);
    	message.setSubject("[회원가입] 이메일 인증");
    	message.setText("아래 링크를 클릭하여 인증을 완료하세요:\n" + link);
    	
    	mailSender.send(message);
    }
}
