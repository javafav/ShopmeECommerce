package com.shopme.product;

import java.text.ParseException;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.ProductNotFoundException;

@Service
public class ProductService {

	public static final int PRODUCTS_PER_PAGE = 10;
	public static final int SEARCH_RESULTS_PER_PAGE = 10;


	@Autowired
	private ProductRepository repo;

	public Page<Product> listProductByCategory(int pageNum, Integer categoryId) {
		String allParentIds = "-" + String.valueOf(categoryId) + "-";

		PageRequest pagebale = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE);

		return repo.listProductByCategory(categoryId, allParentIds, pagebale);

	}
	
	public Page<Product> listByBrand(int pageNum, Integer brandId) {
		Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE);
		
		return repo.listByBrand(brandId, pageable);
	}	

	
	public Product getProduct(String alias) throws ProductNotFoundException {
		Product product = repo.findByAlias(alias);
		if (product == null) {
			throw new ProductNotFoundException("Could not find the product with given alias " + alias);
		}
		return product;
	}

	public Product getProduct(Integer productId) throws ProductNotFoundException {
		try {
			return repo.findById(productId).get();
		} catch (NoSuchElementException ex) {
			throw new ProductNotFoundException("No Product found witn gienve id" + productId);
		}

	}
	
	public Product get(Integer id) throws ProductNotFoundException {
		try {
			
			return repo.findById(id).get();

		} catch (NoSuchElementException ex) {
			throw new ProductNotFoundException("Could not find the product with given (ID " + id + ")");
		}
	}

	public Page<Product> search(String keyword, int pageNum) {

		PageRequest pagebale = PageRequest.of(pageNum - 1, SEARCH_RESULTS_PER_PAGE);
		return repo.search(keyword, pagebale);

	}

	public Page<Product> getProductOnDiscountOrSale(int pageNum) {

		PageRequest pagebale = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE);
		return repo.findAllProductOnSaleOrDiscount(pagebale);

	}

	public Page<Product> getAllProduct(int pageNum) {

		PageRequest pagebale = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE);
		return repo.listAllProduct(pagebale);

	}

	public Page<Product> getAllMostRatedProduct(float avgRating, int pageNum) {

		PageRequest pagebale = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE);
		return repo.listAllMostRatedProduct(avgRating, pagebale);

	}

	public Page<Product> getAllBestSelingProduct(Long quantity, int pageNum) {

		PageRequest pagebale = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE);
		return repo.listAllBestSellingProduct(quantity, pagebale);

	}

	public Page<Product> getAllNewArrivalProduct(int pageNum) throws ParseException {
//
//		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
//		Date startTime = dateFormatter.parse("2020-03-01");
//		Date endTime = dateFormatter.parse("2020-09-30");

		PageRequest pagebale = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE);
		return repo.listAllProductsAddedLastXMonths( pagebale);

	}
}
