package com.shopme.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Product;
import com.shopme.common.exception.ProductNotFoundException;

@Service
public class ProductService {
    
	public static final int PRODUCTS_PER_PAGE = 10;
	public static final int SEARCH_RESULTS_PER_PAGE = 10;
	
	@Autowired
	 private ProductRepository repo;
	
	public Page<Product> listProductByCategory(int pageNum,Integer categoryId){
		String allParentIds = "-" + String.valueOf(categoryId) + "-";
		
		PageRequest pagebale = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE);
		
		return 	repo.listProductByCategory(categoryId, allParentIds, pagebale);

		   
	}
	
	public Product getProduct(String alias) throws ProductNotFoundException {
		Product product = repo.findByAlias(alias);
		if(product == null) {
			throw new ProductNotFoundException("Could not find the product with given alias " + alias);
		}
		return product;
	}
	
	public Page<Product> search(String keyword, int pageNum) {
	
		PageRequest pagebale = PageRequest.of(pageNum - 1, SEARCH_RESULTS_PER_PAGE);
		return repo.search(keyword, pagebale);
		

	}
}
