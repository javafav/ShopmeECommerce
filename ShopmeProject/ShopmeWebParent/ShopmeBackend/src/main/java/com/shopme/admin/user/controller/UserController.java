package com.shopme.admin.user.controller;

import java.io.IOException;
import java.util.List;


import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
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
import com.shopme.admin.user.UserNotFoundException;
import com.shopme.admin.user.UserService;
import com.shopme.admin.user.export.UserCsvExporter;
import com.shopme.admin.user.export.UserExcelExporter;
import com.shopme.admin.user.export.UserPdfExporter;
import com.shopme.common.entity.User;

@Controller
public class UserController {

	@Autowired
	private UserService service;

	@GetMapping("/users")
	public String listFirstPage(Model model) {

		return listByPage(1, "firstName", "asc", null, model);
	}

	@GetMapping("/users/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") int pageNum,
			@RequestParam(name = "sortField") String sortField, @RequestParam(name = "sortDir") String sortDir,
			@RequestParam(name = "keyword") String keyword,

			Model model) {

		Page<User> page = service.listByPage(pageNum, sortField, sortDir, keyword);

		List<User> listUsers = page.getContent();
		long startCount = (pageNum - 1) * UserService.USERS_PER_PAGE + 1;
		long endCount = startCount + UserService.USERS_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}

		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", page.getTotalPages());

		model.addAttribute("listUsers", listUsers);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());

		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);

		return "users/users";
	}

	@GetMapping("/users/export/csv")
	public void exportToCsv(HttpServletResponse response) throws IOException {
		Sort sort = Sort.by(Direction.ASC, "firstName");
		List<User> listUsers = service.listAll(sort);
		UserCsvExporter exporter = new UserCsvExporter();
		exporter.export(listUsers, response);
	}

	@GetMapping("/users/export/excel")
	public void exportToExcel(HttpServletResponse response) throws IOException {
		Sort sort = Sort.by(Direction.ASC, "firstName");
		List<User> listUsers = service.listAll(sort);
		UserExcelExporter exporter = new UserExcelExporter();
		
		exporter.export(listUsers,response);
	}
	@GetMapping("/users/export/pdf")
	public void exportToPdf(HttpServletResponse response) throws IOException {
		Sort sort = Sort.by(Direction.ASC, "firstName");
		List<User> listUsers = service.listAll(sort);
		UserPdfExporter exporter = new UserPdfExporter();
		
		exporter.export(listUsers,response);
	}
	
	
	@GetMapping("/users/new")
	public String createNewUser(Model model) {
		User user = new User();
		user.setEnabled(true);
		model.addAttribute("listRoles", service.listRoles());
		model.addAttribute("user", user);
		model.addAttribute("pageTitle", "Create New User");

		return "users/user_form";
	}

	@PostMapping("/users/save")
	public String saveUser(User user, RedirectAttributes redirectAttributes,
			@RequestParam("image") MultipartFile multipartFile) throws IOException {

		if (!multipartFile.getOriginalFilename().isEmpty()) {

			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			user.setPhotos(fileName);
			User savedUser = service.save(user);
			String uploadDir = "user-photos/" + savedUser.getId();
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} else {
			if (user.getPhotos().isEmpty())
				user.setPhotos(null);
			service.save(user);
		}

		redirectAttributes.addFlashAttribute("message", "The User has been saved successfuly!");
		return getURLOfAffectedUser(user);
	}

	private String getURLOfAffectedUser(User user) {
		String emailFirstPart = user.getEmail().split("@")[0];

		return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + emailFirstPart;
	}

	@GetMapping("/users/edit/{id}")
	public String updateUser(@PathVariable(name = "id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			User user = service.getUser(id);

			model.addAttribute("listRoles", service.listRoles());
			model.addAttribute("user", user);
			model.addAttribute("pageTitle", "Edit User with ID( " + id + " )");
			return "users/user_form";

		} catch (UserNotFoundException e) {

			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/users";

		}

	}

	@GetMapping("/users/delete/{id}")
	public String deleteUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
		try {

			service.deleteUser(id);
			redirectAttributes.addFlashAttribute("message", "The user wih (ID " + id + ")  deleted successfuly!");

			return "redirect:/users";
		} catch (UserNotFoundException e) {

			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/users";

		}

	}

	@GetMapping("/users/{id}/enabled/{status}")
	public String updateUserEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean status,
			RedirectAttributes redirectAttributes) {
		try {
			service.updateUserEnableStatus(id, status);
			String messageEnabledOrDisabled = status == true ? "enabled" : "disabled";
			redirectAttributes.addFlashAttribute("message",
					"The user wih (ID " + id + ") " + messageEnabledOrDisabled + " successfuly!");
		} catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/users";
			
		}
	

		return "redirect:/users";

	}
}
