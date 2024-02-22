package com.shopme;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {


		
		exposeDirectory("../categories-images", registry);
		exposeDirectory("../brands-logos", registry);
		exposeDirectory("../product-images", registry);
		exposeDirectory("../site-logo", registry);


		
	}

	private void exposeDirectory(String pathPattren, ResourceHandlerRegistry registry) {
		
		Path path = Paths.get(pathPattren);
		String absloutePath = path.toFile().getAbsolutePath();
		
		String logicalPath = pathPattren.replace("../", "") + "/**";
		
		registry.addResourceHandler(logicalPath)
		                      .addResourceLocations("file:/" + absloutePath + "/");

		
	}


}