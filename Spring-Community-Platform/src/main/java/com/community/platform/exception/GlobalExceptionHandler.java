package com.community.platform.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(SecurityException.class)
	public String handleSecurityException(SecurityException e, Model model) {
		model.addAttribute("message", e.getMessage());
		return "error/403";
	}  
	
	@ExceptionHandler(IllegalArgumentException.class)
	public String handleIllegalArgumentException(IllegalArgumentException e, Model model) {
		model.addAttribute("message", e.getMessage());
		return "error/404";
	}
}
