package com.shopme.category;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Category;

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
	
	public Category getCategory(String alias) {
		return repo.findByAliasEnabled(alias);
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
