package com.shopme.admin.category;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.shopme.common.entity.Category;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class CategoryServiceTest {

	@MockBean
	private CategoryRepository repo;
	
	@InjectMocks
	private CategoryService service;
	
	@Test
	public void checkUniqueInNewModeReturnDuplicateName() {
		Integer id = 0;
		
		String name = "Computers";
		String alias = "abc";
		
		Category category = new Category(id, name, alias);
		
		Mockito.when(repo.findByName(name)).thenReturn(category);
		Mockito.when(repo.findByAlias(alias)).thenReturn(null);
		String result =  service.checkUnique(id,name, alias);
		
		assertThat(result).isEqualTo("DuplicateName");
		
		
		
	}

	@Test
	public void checkUniqueInNewModeReturnDuplicateAlias() {
		Integer id = 0;
		
		String name = "abc";
		String alias = "Computers";
		
		Category category = new Category(id, name, alias);
		
		Mockito.when(repo.findByName(name)).thenReturn(null);
		Mockito.when(repo.findByAlias(alias)).thenReturn(category);
		String result =  service.checkUnique(id,name, alias);
		
		assertThat(result).isEqualTo("DuplicateAlias");
		
		
		
	}

	@Test
	public void checkUniqueInNewModeReturnOK() {
		Integer id = 0;
		
		String name = "abc";
		String alias = "def";
		
		Category category = new Category(id, name, alias);
		
		Mockito.when(repo.findByName(name)).thenReturn(null);
		Mockito.when(repo.findByAlias(alias)).thenReturn(null);
		String result =  service.checkUnique(id,name, alias);;
		
		assertThat(result).isEqualTo("OK");
		
		
		
	}
	
	@Test
	public void checkUniqueInEditModeReturnDuplicateName() {
		Integer id = 1;
		
		String name = "Computers1";
		String alias = "abc";
		
		Category category = new Category(2, name, alias);
		
		Mockito.when(repo.findByName(name)).thenReturn(category);
		Mockito.when(repo.findByAlias(alias)).thenReturn(null);
		String result = service.checkUnique(id,name, alias);
		
		assertThat(result).isEqualTo("DuplicateName");
		
		
		
	}
	
	@Test
	public void checkUniqueInEditModeReturnDuplicateAlias() {
		Integer id = 1;
		
		String name = "abc";
		String alias = "Computer";
		
		Category category = new Category(2, name, alias);
		
		Mockito.when(repo.findByName(name)).thenReturn(null);
		Mockito.when(repo.findByAlias(alias)).thenReturn(category);
		String result = service.checkUnique(id,name, alias);
		
		assertThat(result).isEqualTo("DuplicateAlias");
		
		
		
	}
	
	@Test
	public void checkUniqueInEditModeReturnOK() {
		Integer id = 1;
		
		String name = "abc";
		String alias = "computers";
		
		Category category = new Category(id, name, alias);
		
		Mockito.when(repo.findByName(name)).thenReturn(null);
		Mockito.when(repo.findByAlias(alias)).thenReturn(category);
		String result =  service.checkUnique(id,name, alias);;
		
		assertThat(result).isEqualTo("OK");
		
		
		
	}

}
