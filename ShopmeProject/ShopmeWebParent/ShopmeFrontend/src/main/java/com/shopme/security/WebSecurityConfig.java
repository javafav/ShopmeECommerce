package com.shopme.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


import com.shopme.customer.CustomerUserDetailsService;
import com.shopme.security.oauth.CustomerOAuth2UserService;
import com.shopme.security.oauth.OAuthLoginSuccessHandler;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired private CustomerOAuth2UserService oAuth2UserService;
	@Autowired private OAuthLoginSuccessHandler oAuthLoginHandlerl;
	@Autowired private DatabaseLoginSuccessHandler dbLoginSuccessHandler;

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.authorizeRequests()
		.antMatchers("/account_details", "/update_account_details", "/orders/**",
				"/cart", "/address_book/**", "/checkout", "/place_order", "/review/**", 
				"/process_paypal_order","/write_review/**", "/post_review").authenticated()
		.anyRequest().permitAll()
		.and()
		.formLogin()
			.loginPage("/login")
			.usernameParameter("email")
			.successHandler(dbLoginSuccessHandler)
			.permitAll()
		.and()
		    .oauth2Login() 
		      .loginPage("/login")
		        .userInfoEndpoint()
		        .userService(oAuth2UserService)
		         .and()
		          .successHandler(oAuthLoginHandlerl)
		 
		.and()
		.logout().permitAll()
		.and()
		.rememberMe()
			.key("1234567890_aBcDeFgHiJkLmNoPqRsTuVwXyZ")
			.tokenValiditySeconds(14 * 24 * 60 * 60)
		;			

				
	}
	
	@Bean
	public UserDetailsService customerUserDetailsService() {
		return new CustomerUserDetailsService();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}


	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/images/**", "/webjars/**", "/js/**");
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(daoAuthenticationProvider());
	}

	public DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(customerUserDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}


}
