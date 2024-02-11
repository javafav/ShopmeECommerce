package com.shopme.admin.setting;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Setting;
import com.shopme.common.entity.SettingCategory;

@Service
public class SettingService {

	@Autowired
	private SettingRepository repo;
	
	
	public List<Setting> listAll(){
		return (List<Setting>) repo.findAll();
	}
	
	public GeneralSettingBag generalSettingBag() {
		List<Setting> setting = new ArrayList<>();
		
		List<Setting> generalSettings = repo.findByCategory(SettingCategory.GENERAL);
        List<Setting> currecnySettings = repo.findByCategory(SettingCategory.CURRENCY);
		
        setting.addAll(generalSettings);
        setting.addAll(currecnySettings);
        
        
        return new GeneralSettingBag(setting);
		
	}
	
	public void saveAll(Iterable<Setting> setting) {
		repo.saveAll(setting);
	}
	
}
