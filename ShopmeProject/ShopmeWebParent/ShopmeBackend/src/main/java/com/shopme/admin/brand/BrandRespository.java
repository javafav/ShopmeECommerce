package com.shopme.admin.brand;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.shopme.common.entity.Brand;

public interface BrandRespository extends PagingAndSortingRepository<Brand, Integer> {

}
