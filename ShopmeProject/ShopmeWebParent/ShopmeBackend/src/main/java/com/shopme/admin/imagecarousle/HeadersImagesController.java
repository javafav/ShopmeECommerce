package com.shopme.admin.imagecarousle;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.AmazonS3Util;
import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.brand.BrandNotFoundException;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.HeadersImages;
import com.shopme.common.exception.ImageCarouselNotFoundException;

@Controller
public class HeadersImagesController {

    @Autowired
    private HeadersImagesService imageCarouselService;

    @GetMapping("/carousels")
    public String getAllImages(Model model) {
        List<HeadersImages> listCarousels = imageCarouselService.getAllImages();
        model.addAttribute("listCarousels", listCarousels);

        return "carousel/carousels"; 
    }

    @GetMapping("/carousels/new")
    public String createImage( Model model) {
    	
    	 long defaultSortOrder = imageCarouselService.getTotalImages() + 1; // Get total images + 1
    	 HeadersImages imageCarousel = new HeadersImages();  
    	 imageCarousel.setSortOrder(defaultSortOrder);
    	    model.addAttribute("defaultSortOrder" , defaultSortOrder ); // Set default sort order
    	    model.addAttribute("maxSortOrder", defaultSortOrder);
    	    model.addAttribute("imageCarousel", imageCarousel);
    	
    	    model.addAttribute("pageTitle", " Create New ImageCarousle");
    	  
    	
        return "carousel/carousels_form";
    }
    
    @PostMapping("/carousels/save")
    public String createImage(@ModelAttribute("newImage") HeadersImages imageCarousel,
    	                      Model model,RedirectAttributes redirectAttributes,
    		                  @RequestParam("image") MultipartFile multipartFile) throws IOException {
        try {
        	  if (imageCarouselService.existsBySortOrder(imageCarousel.getSortOrder())) {
        	      
        		  imageCarouselService.adjustSortOrder(imageCarousel.getSortOrder());
        	    }
        	
        	if (!multipartFile.isEmpty()) {
        		String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
    			imageCarousel.setImageURL(fileName);
        		
    			HeadersImages savedImage = imageCarouselService.saveImage(imageCarousel);
    			String uploadDir = "carousels-images/" +  savedImage.getId();
    			
    			AmazonS3Util.removeFolder(uploadDir);
    			AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());
    			 
        	}else {
        		imageCarouselService.saveImage(imageCarousel);
        		
        	}
        	 redirectAttributes.addFlashAttribute("message","The Carousle is saved successfully!");
        	return "redirect:/carousels";
        } catch (ImageCarouselNotFoundException e) {
        	redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/carousels";
        }
       
    }

    
	@GetMapping("/carousels/edit/{id}")
	public String updateUser(@PathVariable(name = "id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			HeadersImages imageCarousel = imageCarouselService.get(id);

		    model.addAttribute("pageTitle", " Edit Carousle with (ID " + id +")");
		
			model.addAttribute("imageCarousel", imageCarousel);
		    model.addAttribute("defaultSortOrder", imageCarousel.getSortOrder()); 
		    model.addAttribute("maxSortOrder", imageCarouselService.getTotalImages());
		    
		
			 return "carousel/carousels_form";

		} catch (ImageCarouselNotFoundException e) {

			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "carousel/carousels";

		}

	}
	
	
	
	//Created Separate Rest Controller for deletion of the header image

//    @GetMapping("/carousels/delete/{id}")
//    public String deleteImage(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
//        try {
//            imageCarouselService.deleteImage(id);
//            redirectAttributes.addFlashAttribute("message", "The carousle wih (ID " + id + ")  deleted successfuly!");
//            return "redirect:/carousels"; 
//        } catch (ImageCarouselNotFoundException e) {
//        	redirectAttributes.addFlashAttribute("message", e.getMessage());
//            return "redirect:/carousels"; 
//        }
//                                            
//    }
}