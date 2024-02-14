package com.shopme.category;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Category;
import com.shopme.common.exception.CategoryNotFoundException;

@Service
public class CategoryService {

	@Autowired 
	private CategoryRepository repo;
	
	public List<Category> listAllCategoryNoChildren(){
		
		List<Category> listCategoryNoChildren =  new ArrayList<>();
		
		List<Category> listAllEnabledCategory = repo.findAllEnabled();
		
		listAllEnabledCategory.forEach(cat -> {
			if(cat.getChildren() == null || cat.getChildren().size() == 0) {
				listCategoryNoChildren.add(cat);
			}
			
		});
		
		return listCategoryNoChildren;
	}
	
	public Category getCategory(String alias) throws CategoryNotFoundException {
		 Category category = repo.findByAliasEnabled(alias);
		 if(category == null) {
			  throw new CategoryNotFoundException("Could not find the category with given alias " + alias);
		 }
		 return category;
	}
	
	public List<Category> getAllParents(Category child){
		List<Category> listParents = new ArrayList<>();
		
		Category parent = child.getParent();		
		
		while(parent != null) {
			listParents.add(0, parent);
			parent = parent.getParent();
			}
		
		return listParents;
	}
}
