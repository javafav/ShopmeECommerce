package com.shopme.admin.brand;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import com.shopme.admin.category.CategoryService;
import com.shopme.admin.product.ProductService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;


@Controller
public class BrandController {

	@Autowired private BrandService brandService;
	@Autowired private CategoryService categoryService;
	
	
	@GetMapping("/brands")
	public String listFirstPage(Model model) {
		return listByPage(1, "asc", "name",null, model);
	}
	
	@GetMapping("/brands/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") int pageNum,
			 @RequestParam(name = "sortDir") String sortDir,
			 @RequestParam(name = "sortField") String sortField,
			 @RequestParam(name = "keyword") String keyword,

			Model model) {

		Page<Brand> pageBrand = brandService.listByPage(pageNum, sortDir, sortField, keyword);
     
		List<Brand> listBrands = pageBrand.getContent();

		
		long startCount = (pageNum - 1) * BrandService.BRANDS_PER_PAGE + 1;
		long endCount = startCount +  BrandService.BRANDS_PER_PAGE - 1;
		if (endCount > pageBrand.getTotalElements()) {
			endCount = pageBrand.getTotalElements();
		}

		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", pageBrand.getTotalPages());

		model.addAttribute("listBrands", listBrands);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", pageBrand.getTotalElements());

		model.addAttribute("sortField", "name");
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);

		return "brands/brands";
	}

	
	
	
	
	
	
	
	
	@GetMapping("/brands/new")
	public String createNewBrand(Model model) {
		List<Category> listCategories = categoryService.categoryListUsedInForm();
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("pageTitle", " Create New Brand");
		model.addAttribute("brand", new Brand());
		return "brands/brand_form";
	}
	
	@PostMapping("/brands/save")
	public String saveBrand(Brand brand, RedirectAttributes redirectAttributes,
			@RequestParam("image") MultipartFile multipartFile) throws IOException {

		if (!multipartFile.getOriginalFilename().isEmpty()) {

			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			brand.setLogo(fileName);
			
			Brand savedBrand = brandService.save(brand);
			String uploadDir = "../brands-logos/" + savedBrand.getId();
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} else {
			brandService.save(brand);
		}

		redirectAttributes.addFlashAttribute("message", "The brand has been saved successfuly!");
		return"redirect:/brands";
	}
	
	@GetMapping("/brands/edit/{id}")
	public String updateUser(@PathVariable(name = "id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			Brand brand = brandService.get(id);

			List<Category> listCategories = categoryService.categoryListUsedInForm();
			
			model.addAttribute("listCategories", listCategories);
	
			model.addAttribute("pageTitle", " Edit Brnad with (ID " + id +")");
		
			model.addAttribute("brand", brand);
		
			return "brands/brand_form";

		} catch (BrandNotFoundException e) {

			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return"redirect:/brands";

		}

	}
	
	
	@GetMapping("/brands/delete/{id}")
	public String deleteUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
		try {

			brandService.delete(id);
			redirectAttributes.addFlashAttribute("message", "The brand wih (ID " + id + ")  deleted successfuly!");

			return "redirect:/brands";
		} catch (BrandNotFoundException e) {

			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/brands";

		}

	}

}
