package com.shopme.setting;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Setting;
import com.shopme.common.entity.SettingCategory;

@Service
public class SettingService {

	@Autowired
	private SettingRepository repo;
	

	public List<Setting>  generalSettingBag() {
	return repo.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);
		
	}
	

	
}