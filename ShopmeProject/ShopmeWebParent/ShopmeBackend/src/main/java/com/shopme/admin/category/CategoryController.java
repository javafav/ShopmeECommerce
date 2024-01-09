package com.shopme.admin.category;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.User;

@Controller
public class CategoryController {

	@Autowired
	private CategoryService service;
	
	@GetMapping("/categories")
	public String listFirstPage(Model model) {

		List<Category> listCategories = service.listAll();
		
		model.addAttribute("listCategories", listCategories);
	
		return "categories/categories";
	}
	
	
	@GetMapping("/categories/new")
	public String createNewCategory(Model model) {
		List<Category> listCategories = service.categoryListUsedInForm();
		
		model.addAttribute("pageTitle","Create New Category");
		model.addAttribute("category", new Category());
		model.addAttribute("listCategories", listCategories);

		
		return "categories/category_form";
	}
	
	@PostMapping("/categories/save")
	public String saveCategory(Category category, @RequestParam("fileimage") MultipartFile multipartFile,
			                       RedirectAttributes redirectAttributes,Model model) throws IOException {
		
		String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		category.setImage(fileName);
		Category savedCategory = service.save(category);
		String uploadDir = "../category-images/" + savedCategory.getId();
		FileUploadUtil.cleanDir(uploadDir);
		FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		redirectAttributes.addFlashAttribute("message", "The category has been saved successfully!");
		
		return "redirect:/categories";
	}
	
}
