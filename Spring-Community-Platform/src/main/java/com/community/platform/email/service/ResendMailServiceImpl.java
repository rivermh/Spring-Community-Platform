package com.community.platform.email.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@Profile("prod") // Railway(운영) 환경에서만 이 클래스가 활성화
public class ResendMailServiceImpl implements MailService {

    @Value("${resend.api.key}") // Railway Variables에 추가할 이름
    private String apiKey;

    @Value("${app.base.url}") // 설정한 Railway 도메인 주소
    private String baseUrl;

    @Override
    public void sendVerificationMail(String to, String token) {
        String link = baseUrl + "/verify-email?token=" + token;
        String subject = "[회원가입] 이메일 인증 안내";
        String content = "<p>안녕하세요. 아래 링크를 클릭하여 회원가입 인증을 완료해 주세요:</p>" +
                         "<a href=\"" + link + "\">이메일 인증하기</a>";
        
        sendRequest(to, subject, content);
    }

    @Override
    public void sendPasswordResetMail(String to, String token) {
        String link = baseUrl + "/reset-password?token=" + token;
        String subject = "[비밀번호 재설정] 인증 링크 안내";
        String content = "<p>안녕하세요. 비밀번호를 재설정하시려면 아래 링크를 클릭해 주세요:</p>" +
                         "<a href=\"" + link + "\">비밀번호 재설정하기</a>";
        
        sendRequest(to, subject, content);
    }

    private void sendRequest(String to, String subject, String content) {
        RestTemplate restTemplate = new RestTemplate();

        // 1. 헤더 설정 (API Key 인증)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // 2. 요청 바디 설정 (Resend API 규격)
        Map<String, Object> body = new HashMap<>();
        // Resend 무료 티어는 기본적으로 'onboarding@resend.dev'를 발신자로 사용합니다.
        body.put("from", "onboarding@resend.dev"); 
        body.put("to", to);
        body.put("subject", subject);
        body.put("html", content);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                "https://api.resend.com/emails", 
                entity, 
                String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Resend 메일 발송 성공: {}", to);
            } else {
                log.error("Resend 메일 발송 실패: {}", response.getBody());
            }
        } catch (Exception e) {
            log.error("Resend API 호출 중 오류 발생: {}", e.getMessage());
        }
    }
}