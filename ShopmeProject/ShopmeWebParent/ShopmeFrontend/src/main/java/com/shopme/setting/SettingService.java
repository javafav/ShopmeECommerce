package com.shopme.setting;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Currency;
import com.shopme.common.entity.setting.Setting;
import com.shopme.common.entity.setting.SettingCategory;

@Service
public class SettingService {

	@Autowired
	private SettingRepository settingRepo;
	@Autowired
	private CurrencyRepository currencyRepo;

	public  List<Setting> generalSettingBag() {
		return settingRepo.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);

	}

	public EmailSettingBag getEmailSettings() {
		List<Setting> settings = settingRepo.findByCategory(SettingCategory.MAIL_SERVER);
		settings.addAll(settingRepo.findByCategory(SettingCategory.MAIL_TEMPLATES));

		return new EmailSettingBag(settings);
	}

	public CurrencySettingBag getCurrencySettings() {
		List<Setting> settings = settingRepo.findByCategory(SettingCategory.CURRENCY);
		return new CurrencySettingBag(settings);
	}

	public PaymentSettingBag getPaymentSettings() {
		List<Setting> settings = settingRepo.findByCategory(SettingCategory.PAYMENT);
		return new PaymentSettingBag(settings);
	}

	public String getCurrencyCode() {
		
		Setting setting = settingRepo.findByKey("CURRENCY_ID");
		Integer currencyId = Integer.parseInt(setting.getValue());
		Currency currency = currencyRepo.findById(currencyId).get();

		return currency.getCode();

	}
	
	public float TOP_RATED_INDEX() {
		Setting setting = settingRepo.findByKey("MOST_RATED_INDEX");
		Float topRatedIndex = Float.parseFloat(setting.getValue());
		
		return topRatedIndex;
	}

	
	public Long BEST_SELLING_INDEX() {
		Setting setting = settingRepo.findByKey("BEST_SELLING_INDEX");
		long bestSellingIndex = Long.parseLong(setting.getValue());
		
		return bestSellingIndex;
	}

}
