package com.shopme.admin.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

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
		product.setCategory(category);
		
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
	
	@Test
	public void testFindByName() {
		
		String productName = "Samsung M52";
		Product product = repo.findByName( productName);
		
		assertThat(product.getName()).isEqualTo(productName);

	}
	
	@Test
	public void testAddExtramAndMainImage() {
		
		Product product = repo.findById(1).get();
		
		String mainImage = "main-imag.jpeg";
		product.setMainImage(mainImage);
		
	
		String extraImage1 = "extra-image1";
		String extraImage2 = "extra-image2";
		product.addExtraImage(extraImage1);
		product.addExtraImage(extraImage2);

		
		repo.save(product);
		
				
	}
	
	@Test
	public void testAddDetails() {
		
		Product product = repo.findById(1).get();
		
		String name = "Samsung M52";
		String value = "Best Phone of Samsung";
		
	product.addDetail(name, value);
	repo.save(product);
		
	}
}
