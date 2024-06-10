package com.shopme.setting;


import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;


import com.shopme.common.Constants;
import com.shopme.common.entity.HeadersImages;
import com.shopme.common.entity.Menu;
import com.shopme.common.entity.setting.Setting;
import com.shopme.headerimage.HeaderImageService;
import com.shopme.menu.MenuService;
@Component
public class SettingFilter implements Filter {

  
	 @Autowired  private SettingService settingService;
	 @Autowired  private MenuService menuService;
	 @Autowired  private HeaderImageService headerImageService;
	
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) servletRequest;
		String url = request.getRequestURL().toString();
		if(url.endsWith(".css") || url.endsWith(".js") || url.endsWith("png") || url.endsWith("jpeg")) {
			chain.doFilter(request, response);
			return;
		}
		
		List<Setting> settings = settingService.generalSettingBag();
		
		settings.forEach(setting -> {
			request.setAttribute(setting.getKey(), setting.getValue());
		});
		
		request.setAttribute("S3_BASE_URI", Constants.S3_BASE_URI);
		
		loadMenuSettings(request);
		loadImageHeaderSettings(request);
		//setCsrfTokenAndHeaderName(request);
		chain.doFilter(request, response);
		
		
	}
	
	private void loadMenuSettings(ServletRequest request) {
		List<Menu> headerMenuItems = menuService.getHeaderMenuItems();
		request.setAttribute("headerMenuItems", headerMenuItems);

		List<Menu> footerMenuItems = menuService.getFooterMenuItems();
		request.setAttribute("footerMenuItems", footerMenuItems);		
	}
	
	private void loadImageHeaderSettings(ServletRequest request) {
	    // Fetch the settings for the image header section from your service
	    List<HeadersImages> headerImages = headerImageService.getAllHeaderImages();
	    
	    // Set the header images as an attribute in the request object
	    request.setAttribute("headerImages", headerImages);
	}
	
	
//	private void setCsrfTokenAndHeaderName(ServletRequest request) {
//		     ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
//		    CsrfToken csrfToken = (CsrfToken) attr.getRequest().getAttribute(CsrfToken.class.getName());
//		    
//		    request.setAttribute("csrfToken", csrfToken.getToken());
//		    request.setAttribute("csrfHeaderName", csrfToken.getHeaderName());
//	}

}
