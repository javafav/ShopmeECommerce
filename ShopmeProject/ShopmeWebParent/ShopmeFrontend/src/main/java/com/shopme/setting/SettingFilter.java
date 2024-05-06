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

import com.shopme.common.entity.Menu;
import com.shopme.common.entity.setting.Setting;
import com.shopme.menu.MenuService;
@Component
public class SettingFilter implements Filter {

  
	 @Autowired  private SettingService settingService;
	 @Autowired  private MenuService menuService;
	
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
	
		
		loadMenuSettings(request);
		chain.doFilter(request, response);
		
		
	}
	
	private void loadMenuSettings(ServletRequest request) {
		List<Menu> headerMenuItems = menuService.getHeaderMenuItems();
		request.setAttribute("headerMenuItems", headerMenuItems);

		List<Menu> footerMenuItems = menuService.getFooterMenuItems();
		request.setAttribute("footerMenuItems", footerMenuItems);		
	}

}
