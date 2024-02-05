package com.shopme.category;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Category;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CategoryRepositoryTests {

	@Autowired
	private CategoryRepository repo;
	
	@Test
	public void testFindAllEnabled() {
		
		List<Category> listCategoreis = repo.findAllEnabled();
		
		listCategoreis.forEach(cat -> System.out.println("Category Name: " + cat.getName()));
	}
	
	@Test
	public void testFindByAliasEnabled() {
		String alias = "hard_drive";
		Category category = repo.findByAliasEnabled(alias);
		
		assertThat(category).isNotNull();
		
	}
}
