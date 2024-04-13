package com.shopme.product;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.product.Product;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)

public class ProductRepositoryTests {

	@Autowired private ProductRepository repo;
	
	@Test
	public void testSearch() {
		
		String search = "test";
		int pageNum = 2;
		
		PageRequest pagebale = PageRequest.of(pageNum - 1, ProductService.SEARCH_RESULTS_PER_PAGE);
		Page<Product> pageProduct = repo.search("test", pagebale);
		
		List<Product> listProduct = pageProduct.getContent();
		
		listProduct.forEach(proudct -> System.out.println(proudct.getName()));
	}
	@Test
	public void testUpdateReviewCountAndAverageRating() {
		Integer productId = 100;
		repo.updateReviewCountAndAverageRating(productId);
	}
}
