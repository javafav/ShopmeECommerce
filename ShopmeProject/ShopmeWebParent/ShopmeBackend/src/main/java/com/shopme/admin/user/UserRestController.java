package com.shopme.admin.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRestController {
	
	@Autowired private UserService service;

	@PostMapping("/users/email_unique")
	public String checkEmailUniqueness(@RequestParam("email") String email,@RequestParam("id") Integer id) {
		boolean emailUnique = service.isEmailUnique(id,email);
		return emailUnique ? "OK" : "Duplicated";
		 
	}
}
