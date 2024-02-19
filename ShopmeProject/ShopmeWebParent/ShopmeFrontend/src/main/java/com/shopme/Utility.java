package com.shopme;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.shopme.setting.EmailSettingBag;

public class Utility {

	public static String getSiteURL(HttpServletRequest request) {
		String siteURL = request.getRequestURL().toString();
		return siteURL.replaceAll(request.getServletPath(), "");
	}
	

	public static JavaMailSenderImpl prepareMailSender(EmailSettingBag settings) {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		
		mailSender.setHost(settings.getHost());
		mailSender.setPort(settings.getPort());
		mailSender.setUsername(settings.getUsername());
		mailSender.setPassword(settings.getPassword());
	
		
		Properties mailProperties = new Properties();
		mailProperties.setProperty("mail.smtp.auth", settings.getSmtpAuth());
		
		mailProperties.setProperty("mail.smtp.starttls.enable", "true");
		mailProperties.setProperty("mail.smtp.ssl.enable", "true");
		mailSender.setJavaMailProperties(mailProperties);
		
		return mailSender;
	}
	

}
