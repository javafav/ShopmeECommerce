package com.shopme.admin.brand;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

@RestController
public class BrandRestController {

	@Autowired private BrandService service;
	
	@PostMapping("/brands/check_unique")
	public String checkUnique(@RequestParam(name = "id") Integer id,@RequestParam(name = "name") String name) {
		return service.checkUnique(id, name);
	}
	
	@GetMapping("/brands/{id}/categories")
	public List<CategoryDTO> categoryListByBrnad(@PathVariable("id") Integer id) throws BrandNotFoundRestException{
		List<CategoryDTO> categoryList = new ArrayList<>();
		try {
			
			Brand brand = service.get(id);
			Set<Category> listCategories = brand.getCategories();
			
			for(Category category : listCategories) {
				CategoryDTO categoryDTO = new CategoryDTO();
				categoryDTO.setId(category.getId());
				categoryDTO.setName(category.getName());
				categoryList.add(categoryDTO);
				
			}
			return categoryList;
		} catch (BrandNotFoundException e) {
		throw new BrandNotFoundRestException();
		}
		
		
	}
}
