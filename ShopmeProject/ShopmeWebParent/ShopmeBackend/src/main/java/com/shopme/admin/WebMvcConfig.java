package com.shopme.admin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

//		String dirName = "user-photos";
//		Path userPhotoDir = Paths.get(dirName);
//		String userPhtoPath = userPhotoDir.toFile().getAbsolutePath();
//
//		registry.addResourceHandler("/" + dirName + "/**").addResourceLocations("file:/" + userPhtoPath + "/");
	
		
		exposeDirectory("user-photos", registry);
		exposeDirectory("../categories-images", registry);
		exposeDirectory("../brands-logos", registry);
		exposeDirectory("../product-images", registry);
		exposeDirectory("../site-logo", registry);
		
//		String brandLogosDirName = "../brands-logos";
//		Path brandLogosDir = Paths.get(brandLogosDirName);
//		String brandLogosPath = brandLogosDir.toFile().getAbsolutePath();
//
//		registry.addResourceHandler("/brands-logos/**").addResourceLocations("file:/" + brandLogosPath + "/");

		
	}
	
	private void exposeDirectory(String pathPattren, ResourceHandlerRegistry registry) {
		
		Path path = Paths.get(pathPattren);
		String absloutePath = path.toFile().getAbsolutePath();
		
		String logicalPath = pathPattren.replace("../", "") + "/**";
		
		registry.addResourceHandler(logicalPath)
		                      .addResourceLocations("file:/" + absloutePath + "/");

		
	}


}