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
import com.shopme.common.entity.setting.Setting;

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
	
	@PostMapping("/settings/save_mail_server")
	public String saveMailServerSetting(HttpServletRequest request, RedirectAttributes redirectAttributes)
			throws IOException {

		List<Setting> mialServerSetting = service.getMailServerSetting();

		updateSettingValuesFromForm(request, mialServerSetting);

		redirectAttributes.addFlashAttribute("message", "Mail Server settings updated successfully!");
		return "redirect:/settings#mailServer";
	}
	
	@PostMapping("/settings/mail_templates/account_verficition")
	public String saveMailTemplateSettingForAccountVerfiction(HttpServletRequest request, RedirectAttributes redirectAttributes)
			throws IOException {

		List<Setting> mialTemplateSetting = service.getMailTemplatesSetting();

		updateSettingValuesFromForm(request, mialTemplateSetting);

		redirectAttributes.addFlashAttribute("message", "Customer verification template updated successfully!");
		return "redirect:/settings#mailTemplates";
	}
	
	@PostMapping("/settings/mail_templates/forgot_password")
	public String saveMailTemplateSettingForForgotPassword(HttpServletRequest request, RedirectAttributes redirectAttributes)
			throws IOException {

		List<Setting> mialTemplateSetting = service.getMailTemplatesSetting();

		updateSettingValuesFromForm(request, mialTemplateSetting);

		redirectAttributes.addFlashAttribute("message", "Forgot password template updated successfully!");
		return "redirect:/settings#mailTemplates";
	}
	
	@PostMapping("/settings/mail_templates/order_confirmation")
	public String saveMailTemplateSettingForOrderCofirmation(HttpServletRequest request, RedirectAttributes redirectAttributes)
			throws IOException {

		List<Setting> mialTemplateSetting = service.getMailTemplatesSetting();

		updateSettingValuesFromForm(request, mialTemplateSetting);

		redirectAttributes.addFlashAttribute("message", "Order confirmation template updated successfully!");
		return "redirect:/settings#mailTemplates";
	}
	
	@PostMapping("/settings/mail_templates/newsletter_subscription")
	public String saveMailTemplateSettingForNewsLetterSubsription(HttpServletRequest request, RedirectAttributes redirectAttributes)
			throws IOException {

		List<Setting> mialTemplateSetting = service.getMailTemplatesSetting();

		updateSettingValuesFromForm(request, mialTemplateSetting);

		redirectAttributes.addFlashAttribute("message", "Newsletter Subscription template updated successfully!");
		return "redirect:/settings#newsletter_subscription";
	}
	
	@PostMapping("/settings/save_payment")
	public String savePaymentSetttings(HttpServletRequest request, RedirectAttributes ra) {
		List<Setting> paymentSettings = service.getPaymentSettings();
		updateSettingValuesFromForm(request, paymentSettings);
		
		ra.addFlashAttribute("message", "Payment settings have been saved");
		
		return "redirect:/settings#payment";
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
