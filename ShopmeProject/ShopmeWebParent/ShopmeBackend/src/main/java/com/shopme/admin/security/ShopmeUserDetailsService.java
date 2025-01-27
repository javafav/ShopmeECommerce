package com.shopme.admin.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.shopme.admin.user.UserRepository;
import com.shopme.common.entity.User;


public class ShopmeUserDetailsService implements UserDetailsService {

	@Autowired 
	private UserRepository userRepo;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepo.getUserByName(email);
		if(user != null) {
		return new ShopmeUserDetails(user);
		}
			throw new UsernameNotFoundException("User does not exists with given email: " + email);
		
	}

}
