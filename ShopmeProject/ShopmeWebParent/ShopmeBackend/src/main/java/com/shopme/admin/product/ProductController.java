package com.shopme.admin.product;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.brand.BrandService;
import com.shopme.admin.category.CategoryService;
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;
import com.shopme.common.exception.ProductNotFoundException;

@Controller
public class ProductController {

	@Autowired
	private ProductService productService;
	@Autowired
	private BrandService brandService;
	@Autowired
	private CategoryService categoryService;

	@GetMapping("/products")
	public String listAll(Model model) {
		return listByPage(1, "asc", "name",0, null, model);
	}
	
	@GetMapping("/products/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") int pageNum,
			 @RequestParam(name = "sortDir") String sortDir,
			 @RequestParam(name = "sortField") String sortField,
			 @RequestParam(name = "categoryId", required = false)Integer categoryId,
			 @RequestParam(name = "keyword") String keyword,
		     Model model) {

		
		
		
		Page<Product> pageProduct = productService.listByPage(pageNum, sortDir, sortField, keyword, categoryId);
        List<Category> listCategories = categoryService.categoryListUsedInForm();
        
        
       
        
       
		List<Product> listProducts = pageProduct.getContent();
		
		long startCount =  (pageNum - 1) * ProductService.PRODUCTS_PER_PAGE   + 1;
		long endCount = startCount +  ProductService.PRODUCTS_PER_PAGE -1;
		if (endCount > pageProduct.getTotalElements()) {
			endCount = pageProduct.getTotalElements();
		}
  
		Integer dashPage = 6;
		if(pageProduct.getTotalPages() > 10 && pageNum > 5 && pageNum <= pageProduct.getTotalPages()) {
			dashPage = pageProduct.getTotalPages() - 5 ;
		}
		
		
		
		
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		
        if(categoryId != null) model.addAttribute("categoryId", categoryId);
        
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", pageProduct.getTotalPages());
		model.addAttribute("dashPage", dashPage);
		model.addAttribute("listProducts", listProducts);
		model.addAttribute("listCategories", listCategories);

		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", pageProduct.getTotalElements());

		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);

		return "products/products";
	}

	
	
	
	

	@GetMapping("/products/new")
	public String newProduct(Model model) {
		Product product = new Product();
		product.setInStock(true);
		product.setEnabled(true);

		List<Brand> listBrands = brandService.listAll();
		model.addAttribute("numberofExistingExtraImages", 0);
		model.addAttribute("product", product);
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("pageTitle", "Create New Product");

		return "products/product_form";
	}

	@PostMapping("/products/save")
	public String saveProduct(Product product, RedirectAttributes redirectAttributes,
			@RequestParam(value = "fileImage", required = false) MultipartFile mainImageMultipart,
			@RequestParam(value = "extraImage", required = false) MultipartFile[] extraImageMultipart,
			@RequestParam(name = "detailNames", required = false) String[] detailNames,
			@RequestParam(name = "detailValues", required = false) String[] detailValues,
			@RequestParam(name = "detailIDs", required = false) String[] detailIDs,
			@RequestParam(name = "imageIDs", required = false) String[] imageIDs,
			@RequestParam(name = "imageNames", required = false) String[] imageNames,
			@AuthenticationPrincipal ShopmeUserDetails loggedUser) throws IOException {

	if(loggedUser.hasRole("Salesperson")) {
		productService.saveProductPrice(product);
		redirectAttributes.addFlashAttribute("message", "The product has been saved successfuly!");
		return "redirect:/products";
		
	}
		
		ProductSaveHelper.setMainImageName(mainImageMultipart, product);
		ProductSaveHelper.serExisttingExtraImgesNames(imageIDs, imageNames, product);
		ProductSaveHelper.setNewExtraImageNames(extraImageMultipart, product);
		ProductSaveHelper.setDetailNameAndValues(detailIDs,detailNames, detailValues, product);
		Product savedProduct = productService.saveProduct(product);
		ProductSaveHelper.saveUploadImages(mainImageMultipart, extraImageMultipart, savedProduct);
		ProductSaveHelper.	deleteExtraImagesWereRemovedFromForm(product);

		redirectAttributes.addFlashAttribute("message", "The product has been saved successfuly!");
		return "redirect:/products";
	}

	
	@GetMapping("/products/{id}/enabled/{status}")
	public String updateProductEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean status,
			RedirectAttributes redirectAttributes) {
		try {
			productService.updateProductEnableStatus(id, status);
			String messageEnabledOrDisabled = status == true ? "enabled" : "disabled";
			redirectAttributes.addFlashAttribute("message",
					"The product wih (ID " + id + ") " + messageEnabledOrDisabled + " successfuly!");
			return "redirect:/products";
		} catch (ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/products";

		}

	}

	@GetMapping("/products/delete/{id}")
	public String deleteProduct(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
		try {

			productService.deleteProduct(id);
			String mainImageUploadDir = "../product-images/" + id;
			String extraImagesUploadDir = "../product-images/" + id + "/extras";

			FileUploadUtil.removeDir(mainImageUploadDir);
			FileUploadUtil.removeDir(extraImagesUploadDir);

			redirectAttributes.addFlashAttribute("message", "The product  wih (ID " + id + ")  deleted successfuly!");

			return "redirect:/products";
		} catch (ProductNotFoundException e) {

			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/products";

		}

	}

	@GetMapping("/products/edit/{id}")
	public String updateProduct(@PathVariable(name = "id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			Product product = productService.get(id);

			List<Brand> listBrands = brandService.listAll();
			Integer numberofExistingExtraImages = product.getImages().size();
			model.addAttribute("product", product);
			model.addAttribute("listBrands", listBrands);
			model.addAttribute("numberofExistingExtraImages", numberofExistingExtraImages);
			model.addAttribute("pageTitle", " Edit Product with (ID " + id + ")");

			return "products/product_form";

		} catch (ProductNotFoundException e) {

			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/products";

		}

	}
	
	@GetMapping("/products/details/{id}")
	public String detailsProduct(@PathVariable(name = "id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			Product product = productService.get(id);

			model.addAttribute("product", product);
		    return "products/product_detail_modal";

		} catch (ProductNotFoundException e) {

			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/products";

		}

	}

}
