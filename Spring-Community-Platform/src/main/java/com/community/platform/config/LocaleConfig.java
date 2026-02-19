package com.community.platform.config;

import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration
public class LocaleConfig implements WebMvcConfigurer {

	// 1. 세션에 언어 설정을 저장하도록 설정
	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver slr = new SessionLocaleResolver();
		slr.setDefaultLocale(Locale.KOREA); // 기본값 : 한국어
		return slr;
	}
	
	// 2. URL 파라미터(?lang=ja)를 감시해서 언어를 바꿔주는 역할
	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
		lci.setParamName("lang"); // ex) URL 뒤에 ?lang=ko 또는 ? lang=ja를 붙이면 작동함
		return lci;
	}
	
	// 3. 위에서 만든 인터셉터를 시스템에 등록
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}
