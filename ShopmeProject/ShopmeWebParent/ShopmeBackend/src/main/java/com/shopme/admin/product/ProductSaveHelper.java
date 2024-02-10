package com.shopme.admin.product;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.shopme.admin.FileUploadUtil;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.ProductImage;

public class ProductSaveHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductSaveHelper.class);

	
	static void deleteExtraImagesWereRemovedFromForm(Product product) throws IOException {

		String pathDir = "../product-images/" + product.getId() + "/extras";
		Path path = Paths.get(pathDir);

		try {
			Files.list(path).forEach(file -> {
				String filename = file.toFile().getName();
				if (!product.contiansImageName(filename)) {
					try {
						Files.delete(file);
						LOGGER.info("Deleted file " + filename);
					} catch (IOException e) {
						LOGGER.error("Could not delete the file" +filename);
					}
				}
			});
		} catch (IOException e) {
			LOGGER.error("Could not list the directoy"  + pathDir);
		}

	}

	static void serExisttingExtraImgesNames(String[] imageIDs, String[] imageNames, Product product) {
		if (imageIDs == null || imageIDs.length == 0)
			return;
		Set<ProductImage> images = new HashSet<>();

		for (int count = 0; count < imageIDs.length; count++) {
			Integer imageId = Integer.parseInt(imageIDs[count]);
			String imageFileName = imageNames[count];
			
			images.add(new ProductImage(imageId, imageFileName, product));

		}
		product.setImages(images);
	}

	static void setDetailNameAndValues(String[] detailIDs, String[] detailNames, String[] detailValues, Product product) {

		if (detailNames == null || detailNames.length == 0) return;
			

		for (int count = 0; count < detailNames.length; count++) {
			String name = detailNames[count];
			String value = detailValues[count];
            Integer id = Integer.parseInt(detailIDs[count]);
            if(id != 0) {
            	product.addDetail(id, name, value);
            } else if (!name.isEmpty() && !value.isEmpty()) {
				product.addDetail(name, value);
			}
		}

	}

	static void saveUploadImages(MultipartFile mainImageMultipart, MultipartFile[] extraImageMultipart,
			Product savedProduct) throws IOException {

		if (!mainImageMultipart.isEmpty()) {
			String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());

			String uploadDir = "../product-images/" + savedProduct.getId();

			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipart);
		}
		if (extraImageMultipart.length > 0) {
			String uploadDir = "../product-images/" + savedProduct.getId() + "/extras";

			for (MultipartFile multipartFile : extraImageMultipart) {

				if (multipartFile.isEmpty())continue;
					

				String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

				
				FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			}
		}
	}

	static void setNewExtraImageNames(MultipartFile[] extraImageMultipart, Product product) {
		if (extraImageMultipart.length > 0) {
			for (MultipartFile multipartFile : extraImageMultipart) {
				if (!multipartFile.isEmpty()) {
					String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
					if (!product.contiansImageName(fileName)) {
						product.addExtraImage(fileName);
					}

				}
			}
		}

	}

	static void setMainImageName(MultipartFile mainImageMultipart, Product product) {

		if (!mainImageMultipart.isEmpty()) {

			String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
			product.setMainImage(fileName);
		}

	}

}