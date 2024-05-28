package com.shopme.headerimage;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopme.common.entity.HeadersImages;

public interface HeaderImageRepository extends JpaRepository<HeadersImages, Integer> {

	
  public List<HeadersImages> findAll();
}
