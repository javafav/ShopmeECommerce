package com.shopme.common.entity;

import java.beans.Transient;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "headers_images")
public class HeadersImages extends IdBasedEntity {

	@Column(name = "name")
    private String name;

    @Column(name = "caption")
    private String caption;

    @Column(name = "image_URL", nullable = true)
    private String imageURL;
    
    @Column(name = "sort_order", nullable = false)
    private long sortOrder;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

   
   

	public HeadersImages() {}
 
    public HeadersImages( String caption, Integer sortOrder, Boolean isActive) {
    
        this.caption = caption;
        this.sortOrder = sortOrder;
        this.isActive = isActive;
    }

  
  

    public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public long getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(long sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
	@Transient
	public String getImagePath() {
		if (id == null || imageURL == null ) return "/images/image-thumbnail.png";
				
		return "/carousels-images/" + this.id + "/" + this.imageURL;
	}
}
