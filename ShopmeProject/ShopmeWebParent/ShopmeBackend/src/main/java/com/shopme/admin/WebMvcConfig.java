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

		String dirName = "user-photos";
		Path userPhotoDir = Paths.get(dirName);
		String userPhtoPath = userPhotoDir.toFile().getAbsolutePath();

		registry.addResourceHandler("/" + dirName + "/**").addResourceLocations("file:/" + userPhtoPath + "/");

		String categoryImagesDirName = "../category-images";
		Path categoryImagesDir = Paths.get(categoryImagesDirName);
		String categoryImagesPath = categoryImagesDir.toFile().getAbsolutePath();

		registry.addResourceHandler("/category-images/**").addResourceLocations("file:/" + categoryImagesPath + "/");

	}

	public static void cleanDir(String dir) {
		Path dirPath = Paths.get(dir);
		try {
			Files.list(dirPath).forEach(file -> {
				if (Files.isDirectory(file)) {
					try {
						Files.delete(file);
					} catch (IOException e) {
						System.out.print("Could not delete the file" + file);
					}

				}
			});
		} catch (IOException e) {
			System.out.print("Could not list the directory" + dir);

		}
	}

}