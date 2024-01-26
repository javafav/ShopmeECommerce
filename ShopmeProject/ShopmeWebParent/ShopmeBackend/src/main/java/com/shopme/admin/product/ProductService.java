package com.shopme.admin.product;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.admin.user.UserNotFoundException;
import com.shopme.common.entity.Product;

@Service
@Transactional
public class ProductService {

	@Autowired
	private ProductRepository repo;

	public List<Product> listAll() {
		return (List<Product>) repo.findAll();
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

		if (product.getAlias() == null || product.getAlias().isEmpty()) {
			String defaultAlias = product.getName().replaceAll(" ", "-");
			product.setAlias(defaultAlias);
		} else {
			product.setAlias(product.getAlias().replaceAll(" ", "-"));
		}
		if (product.getId() == null) {
			product.setCreatedTime(new Date());
		}

		return repo.save(product);
	}



}
