package com.shopme.admin.setting;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;

import com.shopme.common.entity.Currency;
import com.shopme.common.entity.Setting;

@Controller
public class SettingController {

	@Autowired private SettingService service;
	@Autowired private CurrencyRepository currencyRepo;
	
	@GetMapping("/settings")
	public String viewSettingpage(Model model) {
		
		List<Setting> listSettings = service.listAll();
		List<Currency> listCurrencies = currencyRepo.findAllByOrderByNameAsc();
		
		for(Setting setting  : listSettings) {
			model.addAttribute(setting.getKey(), setting.getValue());
		}
		
	  model.addAttribute("listCurrencies", listCurrencies);
		
		
		return "settings/settings";
	}
	
	
	
	@PostMapping("/settings/save_general")
	public String saveGeneralSetting(@RequestParam("fileImage") MultipartFile multipartFile,
			        HttpServletRequest request, RedirectAttributes redirectAttributes) throws IOException {
		
		GeneralSettingBag settingBag = service.generalSettingBag();
		
		saveSiteLogo(multipartFile, settingBag);
        saveCurrencySymbol(request, settingBag);
		
		updateSettingValuesFromForm(request,settingBag.listAll());
		
		
		redirectAttributes.addFlashAttribute("message", "General settings updated successfully!");
		return "redirect:/settings";
	}
	

	
	private void saveSiteLogo(MultipartFile multipartFile, GeneralSettingBag generalSettingBag) throws IOException {
		
		if (!multipartFile.isEmpty()) {

			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			String value = "/site-logo/" + fileName;
			String uploadDir = "../site-logo/";
			
		   generalSettingBag.updateSiteLog(value);
		   
		    FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		}
	}
	
	private void updateSettingValuesFromForm(HttpServletRequest request, List<Setting> listSettings) {
		for (Setting setting : listSettings) {
			String value = request.getParameter(setting.getKey());
			if (value != null) {
				setting.setValue(value);
			}
		}
		
		service.saveAll(listSettings);
	}
	
	private void saveCurrencySymbol(HttpServletRequest request, GeneralSettingBag settingBag) {
		Integer currencyId = Integer.parseInt(request.getParameter("CURRENCY_ID"));
		Optional<Currency> findByIdResult = currencyRepo.findById(currencyId);
		
		if (findByIdResult.isPresent()) {
			Currency currency = findByIdResult.get();
			settingBag.updateCurrencySymbol(currency.getSymbol());
		}
	}
	
}