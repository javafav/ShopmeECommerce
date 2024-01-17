package com.shopme.common.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;



@Entity
@Table(name = "brands")
public class Brand {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable = false, length = 45, unique = true)
	private String name;
	
	@Column(nullable = false, length = 128)
	private String logo;
	
	@ManyToMany
	@JoinTable(name = "brand_categories",joinColumns = 
	@JoinColumn(name = "brand_id"),
	inverseJoinColumns = 
	@JoinColumn(name = "category_id") )

	private Set<Category> category = new HashSet();

	public Brand() {}
	
	
	
	
	
	public Brand(Integer id, String name) {
		
		this.id = id;
		this.name = name;
		this.logo = "default-logo.png";
	}





	public Brand(String name) {
		this.name = name;
		this.logo = "default-logo.png";
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}



	public Set<Category> getCategory() {
		return category;
	}

	public void setCategory(Set<Category> category) {
		this.category = category;
	}
	
	public void addCategory(Integer id) {
		this.category.add(new Category(id));
	}
	

	@Override
	public String toString() {
		return "Brand [id=" + id + ", name=" + name + ", logo=" + logo + ", caregory=" + category + "]";
	}
	
	

}
