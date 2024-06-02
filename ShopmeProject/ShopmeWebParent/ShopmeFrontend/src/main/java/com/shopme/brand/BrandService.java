package com.shopme.brand;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import com.shopme.common.entity.Brand;

@Service
public class BrandService {

	 
	public static final int BRANDS_PER_PAGE = 10;
	
	@Autowired
	private BrandRepository repo;
	
	public Page<Brand> listByPage(int pageNum){
		Sort sort = Sort.by("name").ascending();
		
		Pageable pageable = PageRequest.of( pageNum - 1, BRANDS_PER_PAGE,sort);
		return repo.findAll(pageable);
	}
}
