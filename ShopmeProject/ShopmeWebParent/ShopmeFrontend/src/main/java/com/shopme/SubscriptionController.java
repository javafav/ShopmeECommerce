package com.shopme;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SubscriptionController {

    @Autowired private EmailService emailService;

    @GetMapping("/subscribe/newsletter")
    public ResponseEntity<String> sendEmailForSubscribingNewsletter(HttpServletRequest request) {
        String email = request.getParameter("emailForSubscription");

        // Call the asynchronous method to send the email
        emailService.sendSubscriptionEmail(request, email);

        // Check for errors in the session (to be checked by the client later)
        if (request.getSession().getAttribute("emailError") != null) {
            String errorMessage = (String) request.getSession().getAttribute("emailError");
            request.getSession().removeAttribute("emailError");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }

        // Return the response immediately
        return ResponseEntity.ok("Subscription email will be sent shortly");
    }
}
