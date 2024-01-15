package com.shopme.admin.category;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Category;

@Service
@Transactional
public class CategoryService {

	public final static int ROOT_CATEGORIES_PER_PAGE = 4;

	@Autowired
	private CategoryRepository repo;

	public List<Category> listByPage(CategoryPageInfo pageInfo, String sortDir, int pageNum, String keyword) {
		
		Sort sort = Sort.by("name");
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

		Pageable pageable = PageRequest.of(pageNum - 1, ROOT_CATEGORIES_PER_PAGE);
        Page<Category> pageCategories = null;

		if (keyword != null && !keyword.isEmpty()) {
			
			pageCategories = repo.search(keyword, pageable);
		} else {
			
			pageCategories = repo.findRootCategories(pageable);
		}
		
		List<Category> rootCategories = pageCategories.getContent();

		pageInfo.setTotalElements(pageCategories.getTotalElements());
		pageInfo.setTotalPages(pageCategories.getTotalPages());

		if (keyword != null && !keyword.isEmpty()) {
			
			List<Category> searchCategories = pageCategories.getContent();
			
			for (Category category : searchCategories) {
				category.setHasChildren(category.getChildren().size() > 0);
			}
			
			return searchCategories;
		} else {
			
			return listHierarchicalCategories(rootCategories, sortDir);
		}

	}

	private List<Category> listHierarchicalCategories(List<Category> rootCategories, String sortDir) {
		List<Category> hierarchicalCategories = new ArrayList<>();

		for (Category rootCategory : rootCategories) {
			hierarchicalCategories.add(Category.copyFull(rootCategory));

			Set<Category> children = sortSubCatgories(rootCategory.getChildren(), sortDir);

			for (Category subCategory : children) {
				String name = "--" + subCategory.getName();
				hierarchicalCategories.add(Category.copyFull(subCategory, name));
				listSubHierarchicalCategories(hierarchicalCategories, subCategory, 1, sortDir);
			}

		}

		return hierarchicalCategories;
	}

	private void listSubHierarchicalCategories(List<Category> hierarchicalCategories, Category parent, int subLevel,
			String sortDir) {
		int newSubLevel = subLevel + 1;

		Set<Category> children = sortSubCatgories(parent.getChildren(), sortDir);

		for (Category subCategory : children) {
			String name = "";
			for (int i = 0; i < newSubLevel; i++) {
				name += "--";

			}
			name += subCategory.getName();
			hierarchicalCategories.add(Category.copyFull(subCategory, name));

			listSubHierarchicalCategories(hierarchicalCategories, subCategory, newSubLevel, sortDir);
		}

	}

	public List<Category> categoryListUsedInForm() {

		List<Category> categoryListUsedInForm = new ArrayList<>();

		List<Category> categoryListInDB = repo.findRootCategories(Sort.by("name").ascending());

		for (Category category : categoryListInDB) {
			if (category.getParent() == null) {

				categoryListUsedInForm.add(Category.copyIdAndName(category));

				Set<Category> children = sortSubCatgories(category.getChildren());
				for (Category child : children) {
					String name = "--" + child.getName();
					categoryListUsedInForm.add(Category.copyIdAndName(child, name));

					listSubCategoriesUsedInForm(categoryListUsedInForm, child, 1);
				}
			}
		}
		return categoryListUsedInForm;
	}

	private void listSubCategoriesUsedInForm(List<Category> categoryListUsedInForm, Category parent, int subLevel) {
		int newSubLevel = subLevel + 1;
		Set<Category> children = sortSubCatgories(parent.getChildren());
		for (Category child : children) {
			String name = "";
			for (int i = 0; i < newSubLevel; i++) {
				name += "--";

			}
			name += child.getName();

			categoryListUsedInForm.add(Category.copyIdAndName(child, name));
			listSubCategoriesUsedInForm(categoryListUsedInForm, child, newSubLevel);
		}
	}

	public Category getCategory(Integer categoryId) throws CategoryNotFoundException {
		try {
			Category category = repo.findById(categoryId).get();
			return category;
		} catch (NoSuchElementException e) {
			throw new CategoryNotFoundException("Could not find the Category with given (ID " + categoryId + ")");
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

	private SortedSet<Category> sortSubCatgories(Set<Category> children) {

		return sortSubCatgories(children, "asc");

	}

	private SortedSet<Category> sortSubCatgories(Set<Category> children, String sortDir) {
		SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {

			@Override
			public int compare(Category cat1, Category cat2) {

				if (sortDir.equals("asc")) {
					return cat1.getName().compareTo(cat2.getName());
				} else {
					return cat2.getName().compareTo(cat1.getName());
				}
			}
		});

		sortedChildren.addAll(children);
		return sortedChildren;
	}

	public void updateEnableStatus(Integer categoryId, boolean status) {
		repo.updateEnablestatus(categoryId, status);
	}

	public void deleteCategory(Integer categoryId) throws CategoryNotFoundException {
		Long countById = repo.countById(categoryId);
		if (countById == 0 || categoryId == null) {
			throw new CategoryNotFoundException("Could not find the category with given (ID " + categoryId + ")");
		}
		repo.deleteById(categoryId);
	}

}
