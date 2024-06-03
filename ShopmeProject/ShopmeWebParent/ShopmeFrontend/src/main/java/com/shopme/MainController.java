package com.shopme;


import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.shopme.category.CategoryService;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.product.Product;
import com.shopme.product.ProductService;


@Controller
public class MainController {


	@Autowired private CategoryService categoryService;
	@Autowired private ProductService productService;

	
	
	
	
	

	
	
	
	
	@GetMapping("")
	public String viewHomePage(Model model) {
		
		List<Category> listCategories = categoryService.listNoChildrenCategories();
		model.addAttribute("listCategories", listCategories);
		
		
		return "index";
	}
	
	
	
	
	@GetMapping("/privacy")
	public String viewPrivacyPage(Model model) {
	return "privacy_policy";
	}
	
	@GetMapping("/login")
	public String viewLoginPage(HttpServletRequest request) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return "login";
		}
		
	
		    
		return "redirect:/";
	}
}
