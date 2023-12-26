package com.shopme.admin.user;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;

import com.shopme.common.entity.User;

@Controller
public class UserController {

	@Autowired
	private UserService service;

	@GetMapping("/users")
	public String listAllUsers(Model model) {
		List<User> listUser = service.listAll();

		model.addAttribute("listUser", listUser);
		return "users";
	}

	@GetMapping("/users/new")
	public String createNewUser(Model model) {
		User user = new User();
		user.setEnabled(true);
		model.addAttribute("listRoles", service.listRoles());
		model.addAttribute("user", user);
		model.addAttribute("pageTitle", "Create New User");

		return "user_form";
	}

	@PostMapping("/users/save")
	public String saveUser(User user,
			RedirectAttributes redirectAttributes,@RequestParam("image")MultipartFile multipartFile) throws IOException {

		if(!multipartFile.getOriginalFilename().isEmpty()) {
		
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			user.setPhotos(fileName);
			User savedUser = service.save(user);
			String uploadDir = "user-photos/"+ savedUser.getId();
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} else {
			if(user.getPhotos().isEmpty()) user.setPhotos(null);
			service.save(user);
		}
		
		redirectAttributes.addFlashAttribute("message", "The User has been saved successfuly!");

		return "redirect:/users";
	}

	@GetMapping("/users/edit/{id}")
	public String updateUser(@PathVariable(name = "id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			User user = service.getUser(id);

			model.addAttribute("listRoles", service.listRoles());
			model.addAttribute("user", user);
			model.addAttribute("pageTitle", "Edit User with ID( " + id + " )");
			return "user_form";

		} catch (UserNotFoundException e) {

			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/users";

		}

	}

	@GetMapping("/users/delete/{id}")
	public String deleteUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
		try {

			service.deleteUser(id);
			redirectAttributes.addFlashAttribute("message", "The user wih (ID:" + id + ")  deleted successfuly!");

			return "redirect:/users";
		} catch (UserNotFoundException e) {

			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/users";

		}

	}

	@GetMapping("/users/{id}/enabled/{status}")
	public String updateUserEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean status,
			RedirectAttributes redirectAttributes) {
		service.updateUserEnableStatus(id, status);
		String messageEnabledOrDisabled = status == true ? "enabled" : "disabled";
		redirectAttributes.addFlashAttribute("message",
				"The user wih (ID:" + id + ") " + messageEnabledOrDisabled + " successfuly!");

		return "redirect:/users";

	}
}
