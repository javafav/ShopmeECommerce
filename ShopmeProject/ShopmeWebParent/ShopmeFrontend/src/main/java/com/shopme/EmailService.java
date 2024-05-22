package com.shopme;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Customer;
import com.shopme.setting.EmailSettingBag;
import com.shopme.setting.SettingService;

@Service
public class EmailService {

    @Autowired private SettingService settingService;
    @Autowired private ControllerHelper controllerHelper;

    @Async
    public void sendSubscriptionEmail(HttpServletRequest request, String email) {
        try {
            EmailSettingBag emailSettings = settingService.getEmailSettings();
            JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
            mailSender.setDefaultEncoding("utf-8");

            String toAddress = email;
            String subject = emailSettings.getSubscriptionSubject();
            String content = emailSettings.getSubscriptionContent();

            Customer customer = controllerHelper.getAuthenticatedCustomer(request);
            if (customer == null) {
                content = content.replace("[Subscriber's Name]", email);
            } else {
                content = content.replace("[Subscriber's Name]", customer.getFullName());
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

            helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
            helper.setTo(toAddress);
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
        } catch (UnsupportedEncodingException | MessagingException e) {
           
        
            request.getSession().setAttribute("emailError", "Failed to send subscription email.");
        }
    }
}

