package com.shopme.security.oauth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.shopme.common.entity.AuthenticationType;
import com.shopme.common.entity.Customer;
import com.shopme.customer.CustomerService;

@Component
public class OAuthLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	@Autowired private CustomerService customerService;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {
	
		
		CustomerOAuth2User principal = (CustomerOAuth2User) authentication.getPrincipal();
		
		String email = principal.getEmail();
		String name = principal.getName();	
		String countryCode = request.getLocale().getCountry();
	
	    Customer customer = customerService.getByEmail(email);
	    if(customer == null) {
	    	customerService.addCustomer(email, name, countryCode);
	    }else {
	    	customerService.updateAuthenticationType(customer, AuthenticationType.GOOGLE );
	    }
		
		
		super.onAuthenticationSuccess(request, response, authentication);
	}

}
