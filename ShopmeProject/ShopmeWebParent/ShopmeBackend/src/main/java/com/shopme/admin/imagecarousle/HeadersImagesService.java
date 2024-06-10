package com.shopme.admin.imagecarousle;


import com.shopme.common.entity.HeadersImages;
import com.shopme.common.exception.ImageCarouselNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class HeadersImagesService {

    private static final int MIN_IMAGES = 3;
    private static final int MAX_IMAGES = 7;

    @Autowired
    private HeadersImagesRepository imageCarouselRepository;

    public List<HeadersImages> getAllImages() {
        return imageCarouselRepository.findAllByOrderBySortOrderAsc();
    }

    public boolean existsBySortOrder(long l) {
     
        return imageCarouselRepository.existsBySortOrder(l);
    }

    public void adjustSortOrder(long l) {
       
        List<HeadersImages> imageCarousels = imageCarouselRepository.findBySortOrderGreaterThanEqual(l);
        for (HeadersImages carousel : imageCarousels) {
            carousel.setSortOrder(carousel.getSortOrder() + 1);
            imageCarouselRepository.save(carousel);
        }
    }
    public HeadersImages saveImage(HeadersImages imageCarousel) throws ImageCarouselNotFoundException {
        List<HeadersImages> currentImages = imageCarouselRepository.findAllByOrderBySortOrderAsc();

        if (currentImages.size() >= MAX_IMAGES) {
            throw new ImageCarouselNotFoundException("Cannot add more images. Maximum limit of " + MAX_IMAGES + " reached.");
        }

        return imageCarouselRepository.save(imageCarousel);
    }
    
    
    public Long getTotalImages() {
        return imageCarouselRepository.count();
    }

    public void deleteImage(Integer id) throws  ImageCarouselNotFoundException {
        List<HeadersImages> currentImages = imageCarouselRepository.findAllByOrderBySortOrderAsc();

        if (currentImages.size() <= MIN_IMAGES) {
            throw new ImageCarouselNotFoundException("Cannot delete image. Minimum limit of " + MIN_IMAGES + " images required.");
        }

        imageCarouselRepository.deleteById(id);
    }

    public HeadersImages get(Integer id) throws ImageCarouselNotFoundException {
	try {
			
			return imageCarouselRepository.findById(id).get();

		} catch (NoSuchElementException ex) {
			throw new ImageCarouselNotFoundException("Could not find the carousle with given (ID " + id + ")");
		}
    }
}
