package com.shopme.admin.product;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

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
import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.ProductNotFoundException;

@Controller
public class ProductController {

  
	private String defaultRedirectURL = "redirect:/products/page/1?sortField=name&sortDir=asc&categoryId=0";

    @Autowired	private ProductService productService;
    @Autowired	private BrandService brandService;
	@Autowired	private CategoryService categoryService;

	
	@GetMapping("/products")
	public String listFirstPage(Model model) {
		return defaultRedirectURL;
	}
	
	@GetMapping("/products/page/{pageNum}")
	public String listByPage(
			@PagingAndSortingParam(listName = "listProducts", moduleURL = "/products") PagingAndSortingHelper helper,
			@PathVariable(name = "pageNum") int pageNum, Model model,
			@RequestParam("categoryId") Integer categoryId
			) {
		
		productService.listByPage(pageNum, helper, categoryId);
		
		List<Category> listCategories = categoryService.categoryListUsedInForm();
		
		if (categoryId != null) model.addAttribute("categoryId", categoryId);
		
		
	
		model.addAttribute("productsPerPage", ProductService.PRODUCTS_PER_PAGE);
		
		model.addAttribute("listCategories", listCategories);
		
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

		if (!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Editor")) {

			if (loggedUser.hasRole("Salesperson")) {
				productService.saveProductPrice(product);
				redirectAttributes.addFlashAttribute("message", "The product has been saved successfuly!");
				return defaultRedirectURL;

			}
		}
	
		
		ProductSaveHelper.setMainImageName(mainImageMultipart, product);
		ProductSaveHelper.serExisttingExtraImgesNames(imageIDs, imageNames, product);
		ProductSaveHelper.setNewExtraImageNames(extraImageMultipart, product);
		ProductSaveHelper.setDetailNameAndValues(detailIDs,detailNames, detailValues, product);
		Product savedProduct = productService.saveProduct(product);
		ProductSaveHelper.saveUploadImages(mainImageMultipart, extraImageMultipart, savedProduct);
		ProductSaveHelper.	deleteExtraImagesWereRemovedFromForm(product);

		redirectAttributes.addFlashAttribute("message", "The product has been saved successfuly!");
		return defaultRedirectURL;
	}

	
	@GetMapping("/products/{id}/enabled/{status}")
	public String updateProductEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean status,
			RedirectAttributes redirectAttributes) {
		try {
			productService.updateProductEnableStatus(id, status);
			String messageEnabledOrDisabled = status == true ? "enabled" : "disabled";
			redirectAttributes.addFlashAttribute("message",
					"The product wih (ID " + id + ") " + messageEnabledOrDisabled + " successfuly!");
			return defaultRedirectURL;
		} catch (ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return defaultRedirectURL;

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

			return defaultRedirectURL;
		} catch (ProductNotFoundException e) {

			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return defaultRedirectURL;

		}

	}

	@GetMapping("/products/edit/{id}")
	public String updateProduct(@PathVariable(name = "id") Integer id, Model model,
			RedirectAttributes redirectAttributes,@AuthenticationPrincipal ShopmeUserDetails loggedUser) {
		try {
 
			boolean isReadOnlyForSalesperson = false;

			if (!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Editor")) {

				if (loggedUser.hasRole("Salesperson")) {
					isReadOnlyForSalesperson = true;

				}
			}
			
			Product product = productService.get(id);

			List<Brand> listBrands = brandService.listAll();
			Integer numberofExistingExtraImages = product.getImages().size();
			
			model.addAttribute("isReadOnlyForSalesperson", isReadOnlyForSalesperson);
			model.addAttribute("product", product);
			model.addAttribute("listBrands", listBrands);
			model.addAttribute("numberofExistingExtraImages", numberofExistingExtraImages);
			model.addAttribute("pageTitle", " Edit Product with (ID " + id + ")");

			return "products/product_form";

		} catch (ProductNotFoundException e) {

			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return defaultRedirectURL;

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
