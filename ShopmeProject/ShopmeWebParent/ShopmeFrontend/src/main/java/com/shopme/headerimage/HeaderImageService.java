package com.shopme.headerimage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.HeadersImages;

@Service
public class HeaderImageService {

    @Autowired
    private HeaderImageRepository headerImageRepository;

    public List<HeadersImages> getAllHeaderImages() {
        return headerImageRepository.findAll();
    }
}
