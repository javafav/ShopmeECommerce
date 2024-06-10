package com.shopme.admin.imagecarousle;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.admin.AmazonS3Util;
import com.shopme.common.exception.ImageCarouselNotFoundException;

@RestController
public class HeadersImagesRestController {

    @Autowired
    private HeadersImagesService imageCarouselService;
                  
    @DeleteMapping("/carousels/delete/{id}")
    public String deleteImage(@PathVariable Integer id) {
        try {
            imageCarouselService.deleteImage(id);
            String headerImagesDir = "carousels-images/" + id;
			AmazonS3Util.removeFolder(headerImagesDir);
            return "The carousel with ID " + id + " was deleted successfully!";
        } catch (ImageCarouselNotFoundException e) {
            return e.getMessage();
        }
    }
}
