package com.shopme.setting;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Setting;
import com.shopme.common.entity.SettingCategory;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class SettingRepositoryTest {

	@Autowired
	private SettingRepository repo;
	
	@Test
	public void testFindByTwoCategories() {
		
		List<Setting> listSettings = repo.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);
		
		listSettings.forEach(System.out::println);
	}
}
