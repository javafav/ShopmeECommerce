package com.shopme.admin.user;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepo;
	@Autowired
	private RoleRepository roleRepo;
	@Autowired
	private PasswordEncoder passwordEncoder;

	public List<User> listAll() {
		return userRepo.findAll();
	}

	public List<Role> listRoles() {
		return (List<Role>) roleRepo.findAll();
	}

	public void save(User user) {
		User existingUser = userRepo.findById(user.getId()).get();
		if(existingUser.getId() != null) {
		
		}
		
		
		
	
		userRepo.save(user);
	}

	private void encodePassword(User user) {
		String encodePassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodePassword);
	}

	public boolean isEmailUnique(Integer id, String email) {

		User userByEmail = userRepo.getUserByName(email);
		if(userByEmail == null) return true;
		boolean isCreatingNew = (id ==  null);
		if(isCreatingNew) {
			if(userByEmail != null) {
				return false;
			}
		}else {
			if(userByEmail.getId() != id) {
				return false;
			}
		}
		return true;
	}

	
	
	
	public User getUser(Integer id) throws UserNotFoundException {

		try {
			return userRepo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new UserNotFoundException("User not found with give ID " + id );
		}
	}
}
