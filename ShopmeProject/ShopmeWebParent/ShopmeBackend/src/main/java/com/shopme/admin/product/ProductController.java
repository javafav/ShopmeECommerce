package com.shopme.admin.product;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.brand.BrandService;
import com.shopme.admin.user.UserNotFoundException;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Product;

@Controller
public class ProductController {

	@Autowired
	private ProductService productService;
	@Autowired
	private BrandService brandService;

	@GetMapping("/products")
	public String listAll(Model model) {
		List<Product> listProducts = productService.listAll();
		model.addAttribute("listProducts", listProducts);
		return "products/products";
	}

	@GetMapping("/products/new")
	public String newProduct(Model model) {
		Product product = new Product();
		product.setInStock(true);
		product.setEnabled(true);

		List<Brand> listBrands = brandService.listAll();

		model.addAttribute("product", product);
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("pageTitle", "Create New Product");

		return "products/product_form";
	}

	@PostMapping("/products/save")
	public String saveProduct(Product product,RedirectAttributes redirectAttributes,
			@RequestParam("fileImage") MultipartFile mainImageMultipart,
			@RequestParam("extraImage") MultipartFile[] extraImageMultipart) throws IOException {


		setMainImageName(mainImageMultipart,product);
		setExtraImageNames(extraImageMultipart,product);
		saveUploadImages(mainImageMultipart,extraImageMultipart,product);

		redirectAttributes.addFlashAttribute("message", "The product has been saved successfuly!");
		return "redirect:/products";
	}

	private void saveUploadImages(MultipartFile mainImageMultipart, MultipartFile[] extraImageMultipart,
			Product product) throws IOException {

		if (!mainImageMultipart.getOriginalFilename().isEmpty()) {
			String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
			Product savedProduct = productService.saveProduct(product);
			String uploadDir = "../product-images/" + savedProduct.getId();
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipart);
		}
		if(extraImageMultipart.length > 0) {
			for(MultipartFile multipartFile : extraImageMultipart) {
				if (!multipartFile.getOriginalFilename().isEmpty()) {
					String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
					Product savedProduct = productService.saveProduct(product);
					String uploadDir = "../product-images/extras/" + savedProduct.getId();
					FileUploadUtil.cleanDir(uploadDir);
					FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
				}
			}
		}

	}

	private void setExtraImageNames(MultipartFile[] extraImageMultipart, Product product) {
		if(extraImageMultipart.length > 0) {
			for(MultipartFile multipartFile : extraImageMultipart) {
				if (!multipartFile.getOriginalFilename().isEmpty()) {
					String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
					product.addExtraImage(fileName);

				}
			}
		}

	}

	private void setMainImageName(MultipartFile mainImageMultipart, Product product) {
	
		if (!mainImageMultipart.getOriginalFilename().isEmpty()) {

			String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
			product.setMainImage(fileName);
		}
		
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
	public String deleteUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
		try {

			productService.deleteProduct(id);
			String mainImageUploadDir = "../product-images/" + id;
			String extraImagesUploadDir = "../product-images/extras/" + id;
			
			FileUploadUtil.removeDir(mainImageUploadDir);
			FileUploadUtil.removeDir(extraImagesUploadDir);

			
			redirectAttributes.addFlashAttribute("message", "The product  wih (ID " + id + ")  deleted successfuly!");

			return "redirect:/products";
		} catch (ProductNotFoundException e) {

			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/products";

		}

	}
}
