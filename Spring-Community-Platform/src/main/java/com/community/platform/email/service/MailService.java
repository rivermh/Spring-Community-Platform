package com.community.platform.email.service;


public interface MailService {

	void sendVerificationMail(String to, String token);
	void sendPasswordResetMail(String to, String token);
}