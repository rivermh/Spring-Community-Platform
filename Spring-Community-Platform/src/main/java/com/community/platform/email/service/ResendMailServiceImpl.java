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
@Profile("prod")
public class ResendMailServiceImpl implements MailService {

    @Value("${resend.api.key:none}") 
    private String apiKey;

    @Value("${app.base-url:https://spring-community-platform-production.up.railway.app}") 
    private String baseUrl;

    @Override
    public void sendVerificationMail(String to, String token) {
        log.info("#### [메일 발송 시도] 수신자: {}, 토큰 존재여부: {}", to, (token != null));
        String link = baseUrl + "/verify-email?token=" + token;
        String subject = "[회원가입] 이메일 인증 안내";
        String content = "<p>안녕하세요. 아래 링크를 클릭하여 회원가입 인증을 완료해 주세요:</p>" +
                         "<a href=\"" + link + "\">이메일 인증하기</a>";
        
        sendRequest(to, subject, content);
    }

    @Override
    public void sendPasswordResetMail(String to, String token) {
        log.info("#### [비밀번호 재설정 시도] 수신자: {}", to);
        String link = baseUrl + "/reset-password?token=" + token;
        String subject = "[비밀번호 재설정] 인증 링크 안내";
        String content = "<p>안녕하세요. 비밀번호를 재설정하시려면 아래 링크를 클릭해 주세요:</p>" +
                         "<a href=\"" + link + "\">비밀번호 재설정하기</a>";
        
        sendRequest(to, subject, content);
    }

    private void sendRequest(String to, String subject, String content) {
        log.info("#### [Resend API 호출 시작] Target: {}", to);
        log.info("#### [설정값 확인] API Key 존재여부: {}, BaseURL: {}", (apiKey != null && !apiKey.equals("none")), baseUrl);

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("from", "onboarding@resend.dev"); 
        body.put("to", to);
        body.put("subject", subject);
        body.put("html", content);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            log.info("#### Resend 서버로 요청을 보냅니다...");
            ResponseEntity<String> response = restTemplate.postForEntity(
                "https://api.resend.com/emails", 
                entity, 
                String.class
            );

            log.info("#### Resend 응답 코드: {}", response.getStatusCode());
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("#### [최종 성공] Resend 메일 발송 완료: {}", to);
            } else {
                log.error("#### [최종 실패] Resend 응답 본문: {}", response.getBody());
            }
        } catch (Exception e) {
            log.error("#### [심각한 에러] Resend API 호출 중 예외 발생!");
            log.error("#### 에러 메시지: {}", e.getMessage());
            e.printStackTrace(); // 에러의 상세 원인(Stacktrace)을 로그에 다 찍습니다.
        }
    }
}