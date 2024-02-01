package com.shopme.admin.product;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.brand.BrandService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.ProductImage;

@Controller
public class ProductController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

	@Autowired
	private ProductService productService;
	@Autowired
	private BrandService brandService;

	@GetMapping("/products")
	public String listAll(Model model) {
		return listByPage(1, "asc", "name", null, model);
	}
	
	@GetMapping("/products/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") int pageNum,
			 @RequestParam(name = "sortDir") String sortDir,
			 @RequestParam(name = "sortField") String sortField,
			 @RequestParam(name = "keyword") String keyword,

			Model model) {

		Page<Product> pageProduct = productService.listByPage(pageNum, sortDir, sortField, keyword);
     
		List<Product> listProducts = pageProduct.getContent();
		long startCount =  (pageNum - 1) * ProductService.PRODUCTS_PER_PAGE   + 1;
		long endCount = startCount +  ProductService.PRODUCTS_PER_PAGE -1;
		if (endCount > pageProduct.getTotalElements()) {
			endCount = pageProduct.getTotalElements();
		}
  
		Integer dashPage = pageProduct.getTotalPages();
		dashPage /= productService.PRODUCTS_PER_PAGE;
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", pageProduct.getTotalPages());
		model.addAttribute("dashPage", dashPage);
		model.addAttribute("listProducts", listProducts);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", pageProduct.getTotalElements());

		model.addAttribute("sortField", "name");
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);

		return "products/products";
	}

	
	
	
	

	@GetMapping("/products/new")
	public String newProduct(Model model) {
		Product product = new Product();
		product.setInStock(true);
		product.setEnabled(true);

		List<Brand> listBrands = brandService.listAll();
		model.addAttribute("numberofExistingExtraImages", 0);
		model.addAttribute("product", product);
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("pageTitle", "Create New Product");

		return "products/product_form";
	}

	@PostMapping("/products/save")
	public String saveProduct(Product product, RedirectAttributes redirectAttributes,
			@RequestParam("fileImage") MultipartFile mainImageMultipart,
			@RequestParam("extraImage") MultipartFile[] extraImageMultipart,
			@RequestParam(name = "detailNames", required = false) String[] detailNames,
			@RequestParam(name = "detailValues", required = false) String[] detailValues,
			@RequestParam(name = "detailIDs", required = false) String[] detailIDs,
			@RequestParam(name = "imageIDs", required = false) String[] imageIDs,
			@RequestParam(name = "imageNames", required = false) String[] imageNames) throws IOException {

		setMainImageName(mainImageMultipart, product);
		serExisttingExtraImgesNames(imageIDs, imageNames, product);
		setNewExtraImageNames(extraImageMultipart, product);
		setDetailNameAndValues(detailIDs,detailNames, detailValues, product);
		Product savedProduct = productService.saveProduct(product);
		saveUploadImages(mainImageMultipart, extraImageMultipart, savedProduct);
		deleteExtraImagesWereRemovedFromForm(product);

		redirectAttributes.addFlashAttribute("message", "The product has been saved successfuly!");
		return "redirect:/products";
	}

	private void deleteExtraImagesWereRemovedFromForm(Product product) throws IOException {

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

	private void serExisttingExtraImgesNames(String[] imageIDs, String[] imageNames, Product product) {
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

	private void setDetailNameAndValues(String[] detailIDs, String[] detailNames, String[] detailValues, Product product) {

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

	private void saveUploadImages(MultipartFile mainImageMultipart, MultipartFile[] extraImageMultipart,
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

	private void setNewExtraImageNames(MultipartFile[] extraImageMultipart, Product product) {
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

	private void setMainImageName(MultipartFile mainImageMultipart, Product product) {

		if (!mainImageMultipart.isEmpty()) {

			String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
			product.setMainImage(fileName);
		}

	}

	@GetMapping("/products/{id}/enabled/{status}")
	public String updateProductEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean status,
			RedirectAttributes redirectAttributes) {
		try {
			productService.updateProductEnableStatus(id, status);
			String messageEnabledOrDisabled = status == true ? "enabled" : "disabled";
			redirectAttributes.addFlashAttribute("message",
					"The product wih (ID " + id + ") " + messageEnabledOrDisabled + " successfuly!");
			return "redirect:/products";
		} catch (ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/products";

		}

	}

	@GetMapping("/products/delete/{id}")
	public String deleteProduct(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
		try {

			productService.deleteProduct(id);
			String mainImageUploadDir = "../product-images/" + id;
			String extraImagesUploadDir = "../product-images/" + id + "/extras";

			FileUploadUtil.removeDir(mainImageUploadDir);
			FileUploadUtil.removeDir(extraImagesUploadDir);

			redirectAttributes.addFlashAttribute("message", "The product  wih (ID " + id + ")  deleted successfuly!");

			return "redirect:/products";
		} catch (ProductNotFoundException e) {

			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/products";

		}

	}

	@GetMapping("/products/edit/{id}")
	public String updateProduct(@PathVariable(name = "id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			Product product = productService.get(id);

			List<Brand> listBrands = brandService.listAll();
			Integer numberofExistingExtraImages = product.getImages().size();
			model.addAttribute("product", product);
			model.addAttribute("listBrands", listBrands);
			model.addAttribute("numberofExistingExtraImages", numberofExistingExtraImages);
			model.addAttribute("pageTitle", " Edit Product with (ID " + id + ")");

			return "products/product_form";

		} catch (ProductNotFoundException e) {

			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/products";

		}

	}
	
	@GetMapping("/products/details/{id}")
	public String detailsProduct(@PathVariable(name = "id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			Product product = productService.get(id);

			model.addAttribute("product", product);
		    return "products/product_detail_modal";

		} catch (ProductNotFoundException e) {

			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/products";

		}

	}

}
