package com.shopme.admin.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Date;

import javax.persistence.EntityManager;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.admin.product.ProductRepository;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ProductRepositoryTests {

	@Autowired private ProductRepository repo;
	
	@Autowired private EntityManager entityManager;
	
	@Test
	public void testCreateProduct() {
		
		Brand brand = entityManager.find(Brand.class, 10);
		
		Category category = entityManager.find(Category.class, 10);
		
		Product product = new Product();
		
		product.setBrand(brand);
		product.setCatgory(category);
		
		product.setName("Acer Laptops");
		product.setAlias("Acer-Laptops");
		product.setShortDescription("Short descrption for  Samsung M52");
		product.setFullDescription("Full descrption for  Samsung M52");
		
		product.setCreatedTime(new Date());
		product.setInStock(true);
		product.setPrice(678.4f);
		
		Product savedProduct = repo.save(product);
		
		assertThat(savedProduct).isNotNull();
		assertThat(savedProduct.getId()).isGreaterThan(0);
		
		
	}
}
