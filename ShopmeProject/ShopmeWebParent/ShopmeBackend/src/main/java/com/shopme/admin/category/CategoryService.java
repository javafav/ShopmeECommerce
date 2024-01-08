package com.shopme.admin.category;

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
	
	public List<Category> listAll(){
		return (List<Category>) repo.findAll();
	}
	
	
	public List<Category> categoryListUsedInForm(){
	
		List<Category> categoryListUsedInForm = new ArrayList<>();
		
		List<Category> categoryListInDB = (List<Category>) repo.findAll();

		for (Category category : categoryListInDB) {
			if (category.getParent() == null) {
				System.out.println(category.getName());
				categoryListUsedInForm.add(new Category(category.getName()));

				Set<Category> children = category.getChildren();
				for (Category child : children) {
					String name = "--" + child.getName();
					categoryListUsedInForm.add(new Category(name));

					listChildren(categoryListUsedInForm, child, 1);
				}
			}
		}
		return categoryListUsedInForm;
	}
	
	private void listChildren(List<Category> categoryListUsedInForm,Category parent, int subLevel) {
		int newSubLevel = subLevel + 1;
	
		for (Category child : parent.getChildren()) {
			String name = "" ;
			for (int i = 0; i < newSubLevel; i++) {
				 name += "--";
				
			}
			name += child.getName();
			categoryListUsedInForm.add(new Category(name));

			listChildren(categoryListUsedInForm,child, newSubLevel);
		}
	}
}
