package com.shopme.admin.imagecarousle;

import com.shopme.common.entity.HeadersImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface HeadersImagesRepository extends JpaRepository<HeadersImages, Integer> {
   
	List<HeadersImages> findAllByOrderBySortOrderAsc();
    boolean existsBySortOrder(long sortOrder);
    List<HeadersImages> findBySortOrderGreaterThanEqual(long sortOrder);
}