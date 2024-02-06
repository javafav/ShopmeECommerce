package com.shopme.admin.product;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.admin.brand.BrandNotFoundException;
import com.shopme.admin.user.UserNotFoundException;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Product;
import com.shopme.common.exception.ProductNotFoundException;

@Service
@Transactional
public class ProductService {
	 public static final int PRODUCTS_PER_PAGE = 5;
	@Autowired
	private ProductRepository repo;

	public List<Product> listAll() {
		return (List<Product>) repo.findAll();
	}
	
    public Page<Product> listByPage(int pageNum,String sortDir, String sortField,String keyword,Integer categoryId) {
		
		Sort sort = Sort.by(sortField);
		
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		
		
		
		Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE, sort);
		
		if(keyword != null && !keyword.isEmpty() ) {
		
			if(categoryId != null && categoryId > 0 ) {
				String allParentIds = "-" + String.valueOf(categoryId) + "-";
				return repo.searchInCategories(categoryId, allParentIds, keyword, pageable);
			}
			
			
			return repo.findAll( keyword ,pageable );
			
		}
		if(categoryId != null && categoryId > 0 ) {
			String allParentIds = "-" + String.valueOf(categoryId) + "-";
			return repo.findAllInCategories(categoryId, allParentIds, pageable);
		}
	
		
		return repo.findAll(pageable);
		
		
	
		
	
	}
	
	

	public void updateProductEnableStatus(Integer productId, boolean status) throws ProductNotFoundException {
		try {
			Product product = repo.findById(productId).get();
			if (product != null) {
				repo.updateEnabledStatus(productId, status);
			}
		} catch (NoSuchElementException ex) {
			throw new ProductNotFoundException("Product not found with given ID " + productId);
		}

	}

	public void deleteProduct(Integer id) throws ProductNotFoundException {
		Long countById = repo.countById(id);
		if (countById == 0 || countById == null) {
			throw new ProductNotFoundException("Product not found with given ID " + id);

		}
		repo.deleteById(id);
	}

	

	public String checkUnique(Integer id, String name) {
		boolean isCreatingNew = (id == null || id == 0);
		Product productByName = repo.findByName(name);
		
		if(isCreatingNew) {
		   
			if(productByName != null) return "Duplicate";
			
		}else {
			if(productByName != null && productByName.getId() != id) return "Duplicate";
		}
		
		return "OK";
	}
	
	
	public Product saveProduct(Product product) {

		product.setEnabled(true);
		product.setInStock(true);
		
		
		if(product.getCreatedTime() == null) {
			product.setCreatedTime(new Date());
		} else {
			product.setUpdatedTime(new Date());
		}
		
		if (product.getAlias() == null || product.getAlias().isEmpty()) {
			String defaultAlias = product.getName().replaceAll(" ", "-");
			product.setAlias(defaultAlias);
		} else {
			product.setAlias(product.getAlias().replaceAll(" ", "-"));
		}
	
		return repo.save(product);
	}

	public void saveProductPrice(Product produntInForm) {
		Product productInDB = repo.findById(produntInForm.getId()).get();
		
		productInDB.setCost(produntInForm.getCost());
		productInDB.setPrice(produntInForm.getPrice());
		productInDB.setDiscountPercent(produntInForm.getDiscountPercent());
		
		repo.save(productInDB);
	}

	public Product get(Integer id) throws ProductNotFoundException {
		try {
			
			return repo.findById(id).get();

		} catch (NoSuchElementException ex) {
			throw new ProductNotFoundException("Could not find the product with given (ID " + id + ")");
		}
	}
	
	

}
