package com.shopme.admin.brand;

import static org.assertj.core.api.Assertions.assertThat;



import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class BrandRepositoryTests {

	@Autowired 	private BrandRepository repo;

	
	@Test
	public void testCreateBrand() {
		
		String name = "Samsung";
		Brand brand = new Brand(1,name);
		
		brand.getCategories().add(new Category(14));
		brand.getCategories().add(new Category(9));
		Brand savedBrand = repo.save(brand);
		assertThat(savedBrand.getId()).isGreaterThan(0);
		
	}
	
	
	@Test
	public void testCreateBrand2() {
		
		String name = "Ufone";
		Brand brand = new Brand(4,name);
		
		brand.getCategories().add(new Category(2));
	
		Brand savedBrand = repo.save(brand);
		assertThat(savedBrand.getId()).isGreaterThan(0);
		
	}
	@Test
	public void testListAll() {
		Iterable<Brand> listBrands = repo.findAll();
		listBrands.forEach(brand  -> System.out.println(brand.getName()));
	}
	
	@Test
	public void testUpdateBrand(){
		Integer brandId = 1;
		Brand brand = repo.findById(brandId).get();
		brand.setName("Samsung Electronics");
		Brand savedBrand = repo.save(brand);
		
		assertThat(savedBrand.getName()).isEqualTo("Samsung Electronics");
		
	}
	@Test
	public void testDeleteBran() {
		repo.findById(1).get();
	}
}
