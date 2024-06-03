package com.shopme.product;
import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.ControllerHelper;
import com.shopme.category.CategoryService;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Question;
import com.shopme.common.entity.Review;
import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.CategoryNotFoundException;
import com.shopme.common.exception.ProductNotFoundException;
import com.shopme.question.QuestionService;
import com.shopme.question.vote.QuestionVoteService;
import com.shopme.review.ReviewService;
import com.shopme.review.vote.ReviewVoteService;
import com.shopme.setting.SettingService;


@Controller
public class ProductController {


	@Autowired	private CategoryService categoryService;
	@Autowired  private ProductService productService;
	@Autowired private ReviewService reviewService;
	@Autowired private ControllerHelper controllerHelper;
	@Autowired private  ReviewVoteService voteService;
	@Autowired private QuestionVoteService questionVoteService;
	@Autowired private QuestionService questionService;
	@Autowired private SettingService settingService;
	
	
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
	public String viewProductDetail(@PathVariable("alias") String alias, Model model, HttpServletRequest request) {
		try {
	

			Product product = productService.getProduct(alias);
			
			List<Category> listCategoryParents = categoryService.getAllParents(product.getCategory());
			List<Question> listQuestions = questionService.getTop3VotedQuestions(product.getId());
		
			Page<Review> listReviews = reviewService.list3MostRecentReviewsByProduct(product);
			
			Customer customer =controllerHelper.getAuthenticatedCustomer(request);
			

			
			if(customer != null) {
		
				boolean customerReviewed = reviewService.didCustomerReviewProduct(customer, product.getId());
				voteService.markReviewsVotedForProductByCustomer(listReviews.getContent(), product.getId(), customer.getId());
				questionVoteService.markQuestionsVotedForProductByCustomer(listQuestions, product.getId(), customer.getId());
			
			if (customerReviewed) {
				model.addAttribute("customerReviewed", customerReviewed);
			} else {
				boolean customerCanReview = reviewService.canCustomerReviewProduct(customer, product.getId());
				model.addAttribute("customerCanReview", customerCanReview);
			 }
		}
			
			int numberOfQuestions = questionService.getNumberOfQuestions(product.getId());
			int numberOfAnsweredQuestions = questionService.getNumberOfAnsweredQuestions(product.getId());
			
			model.addAttribute("listQuestions", listQuestions);			
			model.addAttribute("numberOfQuestions", numberOfQuestions);
			model.addAttribute("numberOfAnsweredQuestions", numberOfAnsweredQuestions);
			
			model.addAttribute("name", product.getName());
			model.addAttribute("product", product);
			model.addAttribute("pageTitle", product.getName());
			model.addAttribute("listReviews", listReviews);
			model.addAttribute("listCategoryParents", listCategoryParents);
			
			return "product/product_detail";

			
		
		}catch(ProductNotFoundException ex) {
		 model.addAttribute("product", "product");
			return "error/404";
		}
	}
	
	@GetMapping("/products/details/{id}")
	public String detailsProduct(@PathVariable(name = "id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			Product product = productService.get(id);

			model.addAttribute("product", product);
		    return "product/product_detail_modal";

		} catch (ProductNotFoundException e) {

			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/404";
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
	
	
	@GetMapping("/list_all_product")
	public String listFirstPageOfProductByNameOrderByAsc(Model model) {
		return listAllPageOfProductByNameOrderByAsc(1, model);
	}

	@GetMapping("/list_all_product/page/{pageNum}")
	public String listAllPageOfProductByNameOrderByAsc(@PathVariable("pageNum") Integer pageNum, Model model) {

		Page<Product> pageProduct = productService.getAllProduct(pageNum);
		List<Product> listProducts = pageProduct.getContent();

		long startCount = (pageNum - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
		long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;
		if (endCount > pageProduct.getTotalElements()) {
			endCount = pageProduct.getTotalElements();
		}

		
		model.addAttribute("listProducts", listProducts);
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", pageProduct.getTotalPages());

		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);

		model.addAttribute("listProducts", listProducts);
		model.addAttribute("totalItems", pageProduct.getTotalElements());
		
		return "product/all_product";
	}
	

	@GetMapping("/best_selling_product")
	public String listFirstPageOfBestSellingProductByNameOrderByDesc(Model model) {
		return listAllPageOfBestSellingProductByNameOrderByDesc(1, model);
	}

	@GetMapping("/best_selling_product/page/{pageNum}")
	public String listAllPageOfBestSellingProductByNameOrderByDesc(@PathVariable("pageNum") Integer pageNum, Model model) {

	
		
		Page<Product> pageProduct = productService.getAllBestSelingProduct(settingService.BEST_SELLING_INDEX(), pageNum);
		List<Product> listProducts = pageProduct.getContent();

		long startCount = (pageNum - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
		long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;
		if (endCount > pageProduct.getTotalElements()) {
			endCount = pageProduct.getTotalElements();
		}

		
		model.addAttribute("listProducts", listProducts);
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", pageProduct.getTotalPages());

		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);

		model.addAttribute("listProducts", listProducts);
		model.addAttribute("totalItems", pageProduct.getTotalElements());
		
		return "product/best_selling_product";
	}
	
	
	@GetMapping("/top_rated_product")
	public String listFirstPageOfMostRatedProductByNameOrderByAsc(Model model) {
		return listAllPageOfMostRatedProductByNameOrderByAsc(1, model);
	}

	@GetMapping("/top_rated_product/page/{pageNum}")
	public String listAllPageOfMostRatedProductByNameOrderByAsc(@PathVariable("pageNum") Integer pageNum, Model model) {

		
	
		
		Page<Product> pageProduct = productService.getAllMostRatedProduct(settingService.TOP_RATED_INDEX(), pageNum);
		List<Product> listProducts = pageProduct.getContent();

		long startCount = (pageNum - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
		long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;
		if (endCount > pageProduct.getTotalElements()) {
			endCount = pageProduct.getTotalElements();
		}

		
		model.addAttribute("listProducts", listProducts);
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", pageProduct.getTotalPages());

		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);

		model.addAttribute("listProducts", listProducts);
		model.addAttribute("totalItems", pageProduct.getTotalElements());
		
		return "product/top_rated_product";
	}
	
	
	
	@GetMapping("/new_arrival_product")
	public String listFirstPageNewArrivalProductByNameOrderByAsc(Model model) {
		return listAllPageOfNewArrivalProductByNameOrderByAsc(1, model);
	}

	@GetMapping("/new_arrival_product/page/{pageNum}")
	public String listAllPageOfNewArrivalProductByNameOrderByAsc(@PathVariable("pageNum") Integer pageNum, Model model) {

		
		Page<Product> pageProduct;
		try {
			pageProduct = productService.getAllNewArrivalProduct(pageNum);
		} catch (ParseException e) {
		   return "error";
		}
		List<Product> listProducts = pageProduct.getContent();

		long startCount = (pageNum - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
		long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;
		if (endCount > pageProduct.getTotalElements()) {
			endCount = pageProduct.getTotalElements();
		}

		
		model.addAttribute("listProducts", listProducts);
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", pageProduct.getTotalPages());

		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);

		model.addAttribute("listProducts", listProducts);
		model.addAttribute("totalItems", pageProduct.getTotalElements());
		
		return "product/new_arrival_product";
	}
	
	@GetMapping("/clearance_sale")
	public String listFirstPage(Model model) {
		return viewNewIndexPage(1,model);
	}
	
	
	@GetMapping("/clearance_sale/page/{pageNum}")
	public String viewNewIndexPage(@PathVariable("pageNum") int pageNum, Model model) {
	  Page<Product> page = productService.getProductOnDiscountOrSale(pageNum);
	  List<Product> listProducts = page.getContent();
	  
	 
	 
	  
	    model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("listProducts", listProducts);
		model.addAttribute("moduleURL", "/newindex");
		
       long startCount = (pageNum - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
		model.addAttribute("startCount", startCount);

		long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}

		model.addAttribute("endCount", endCount);
		
	  
	  return "product/clearance_sale";
	}
	
	
	

}
