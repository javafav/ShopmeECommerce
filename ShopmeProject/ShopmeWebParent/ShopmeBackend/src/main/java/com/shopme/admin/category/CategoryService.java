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

	public List<Category> listAll() {
		List<Category> rootCategories = repo.findAllRootCategories();
		return listHierarchicalCategories(rootCategories);
	}

	private List<Category> listHierarchicalCategories(List<Category> rootCategories) {
		List<Category> hierarchicalCategories = new ArrayList<>();

		for (Category rootCategory : rootCategories) {
			hierarchicalCategories.add(Category.copyFull(rootCategory));
			Set<Category> children = rootCategory.getChildren();
			for (Category subCategory : children) {
				String name = "--" + subCategory.getName();
				hierarchicalCategories.add(Category.copyFull(subCategory, name));
				listSubHierarchicalCategories(hierarchicalCategories, subCategory, 1);
			}

		}

		return hierarchicalCategories;
	}

	private void listSubHierarchicalCategories(List<Category> hierarchicalCategories, Category parent, int subLevel) {
		int newSubLevel = subLevel + 1;

		Set<Category> children = parent.getChildren();

		for (Category subCategory : children) {
			String name = "";
			for (int i = 0; i < newSubLevel; i++) {
				name += "--";

			}
			name += subCategory.getName();
			hierarchicalCategories.add(Category.copyFull(subCategory, name));

			listSubHierarchicalCategories(hierarchicalCategories, subCategory, newSubLevel);
		}

	}

	public List<Category> categoryListUsedInForm() {

		List<Category> categoryListUsedInForm = new ArrayList<>();

		List<Category> categoryListInDB = (List<Category>) repo.findAll();

		for (Category category : categoryListInDB) {
			if (category.getParent() == null) {

				categoryListUsedInForm.add(Category.copyIdAndName(category));

				Set<Category> children = category.getChildren();
				for (Category child : children) {
					String name = "--" + child.getName();
					categoryListUsedInForm.add(Category.copyIdAndName(child, name));

					listChildren(categoryListUsedInForm, child, 1);
				}
			}
		}
		return categoryListUsedInForm;
	}

	private void listChildren(List<Category> categoryListUsedInForm, Category parent, int subLevel) {
		int newSubLevel = subLevel + 1;

		for (Category child : parent.getChildren()) {
			String name = "";
			for (int i = 0; i < newSubLevel; i++) {
				name += "--";

			}
			name += child.getName();

			categoryListUsedInForm.add(Category.copyIdAndName(child, name));
			listChildren(categoryListUsedInForm, child, newSubLevel);
		}
	}

	public Category save(Category category) {

		return repo.save(category);
	}

	public String checkUnique(Integer id, String name, String alias) {
		boolean isCreatingNew = (id == null || id == 0);
		
		Category categoryByName = repo.findByName(name);
		
		if (isCreatingNew) {
			if (categoryByName != null) {
				return "DuplicateName";
			} else {
				Category categoryByAlias = repo.findByAlias(alias);
				if (categoryByAlias != null) {
					return "DuplicateAlias";	
				}
			}
		} else {
			if (categoryByName != null && categoryByName.getId() != id) {
				return "DuplicateName";
			}
			
			Category categoryByAlias = repo.findByAlias(alias);
			if (categoryByAlias != null && categoryByAlias.getId() != id) {
				return "DuplicateAlias";
			}
			
		}
		
		return "OK";
	}
}
