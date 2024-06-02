package com.shopme.brand;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.shopme.common.entity.Brand;
import com.shopme.product.ProductService;


@Controller
public class BrandController {

	@Autowired private BrandService service;
	
	
	@GetMapping("/brands")
	public String listFirstPage(Model model) {
		return listByPage(1, model);
	}
	
	@GetMapping("/brands/page/{pageNum}")
	public String listByPage( @PathVariable(name = "pageNum") int pageNum, Model model) {

	    Page<Brand> page = service.listByPage(pageNum);
	    List<Brand> listBrands = page.getContent();
  
		long startCount =  (pageNum - 1) * ProductService.PRODUCTS_PER_PAGE   + 1;
		long endCount = startCount +  ProductService.PRODUCTS_PER_PAGE -1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
       
		model.addAttribute("sortField", "name");
		model.addAttribute("sortDir", "asc");
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);	
		model.addAttribute("listBrands", listBrands);	
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		
		
		return "brand/brands";
	}

}
