package com.shopme.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.shopme.category.CategoryService;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.CategoryNotFoundException;
import com.shopme.common.exception.ProductNotFoundException;

@Controller
public class ProductController {


	@Autowired	private CategoryService categoryService;
	@Autowired  private ProductService productService;
	
	@GetMapping("/c/{category_alias}")
	public String viewCategoryFirstPage(@PathVariable(name = "category_alias") String alias,Model model) { 
		return viewCategoryByPage(alias, 1, model);
	}
	
	
	@GetMapping("/c/{category_alias}/page/{pageNum}")
	public String viewCategoryByPage(@PathVariable(name = "category_alias") String alias,
	                                 @PathVariable(name = "pageNum") Integer pageNum,
	                                 Model model) {
		
		Category category;
		try {
			category = categoryService.getCategory(alias);
		
			Page<Product> pageProduct = productService.listProductByCategory(pageNum, category.getId());
			
			List<Product> listProducts = pageProduct.getContent();
			
			List<Category> listCategoryParents = categoryService.getAllParents(category);
		
			
			
			long startCount =  (pageNum - 1) * ProductService.PRODUCTS_PER_PAGE   + 1;
			long endCount = startCount +  ProductService.PRODUCTS_PER_PAGE -1;
			if (endCount > pageProduct.getTotalElements()) {
				endCount = pageProduct.getTotalElements();
			}

	        
			model.addAttribute("currentPage", pageNum);
			model.addAttribute("totalPages", pageProduct.getTotalPages());
			model.addAttribute("category", category);
			
			model.addAttribute("listProducts", listProducts);
			model.addAttribute("startCount", startCount);
			model.addAttribute("endCount", endCount);
			model.addAttribute("totalItems", pageProduct.getTotalElements());
			
			model.addAttribute("pageTitle", category.getName());
			model.addAttribute("listCategoryParents", listCategoryParents);
			
			return "product/product_by_category";
		
		
		
		} catch (CategoryNotFoundException e) {
			return "error/404";
		}
	
		
	}
	
	@GetMapping("/p/{alias}")
	public String productDetail(@PathVariable("alias") String alias, Model model) {
		try {
	

			Product product = productService.getProduct(alias);
			List<Category> listCategoryParents = categoryService.getAllParents(product.getCategory());
			
			model.addAttribute("name", product.getName());
			model.addAttribute("product", product);
			model.addAttribute("pageTitle", product.getName());
			model.addAttribute("listCategoryParents", listCategoryParents);
			
			return "product/product_detail";

			
		
		}catch(ProductNotFoundException ex) {
		 model.addAttribute("product", "product");
			return "error/404";
		}
	}
	
	@GetMapping("/search")
	public String searchProductzFirstPage(@RequestParam("keyword") String keyword, Model model) {
		return searchProductByPage(keyword, 1, model);
	}
	
	@GetMapping("/search/page/{pageNum}")
	public String searchProductByPage(@RequestParam("keyword") String keyword,@PathVariable("pageNum") Integer pageNum, Model model) {
		
		Page<Product> pageProduct = productService.search(keyword, pageNum);
		List<Product> listProducts = pageProduct.getContent();
		

		long startCount =  (pageNum - 1) * ProductService.SEARCH_RESULTS_PER_PAGE   + 1;
		long endCount = startCount +  ProductService.SEARCH_RESULTS_PER_PAGE -1;
		if (endCount > pageProduct.getTotalElements()) {
			endCount = pageProduct.getTotalElements();
		}

        
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", pageProduct.getTotalPages());
		model.addAttribute("keyword", keyword);

		
	
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		
		model.addAttribute("listProducts", listProducts);
		model.addAttribute("totalItems", pageProduct.getTotalElements());
		
		
		return "product/search_result";
	}
}
