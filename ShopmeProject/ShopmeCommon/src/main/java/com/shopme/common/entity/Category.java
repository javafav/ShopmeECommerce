package com.shopme.common.entity;

import java.beans.Transient;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "categories")
public class Category {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, length = 128, unique = true)
	private String name;

	@Column(nullable = false, length = 64, unique = true)
	private String alias;

	private boolean enabled;

	@Column(nullable = false, length = 128)
	private String image;

	@OneToOne
	@JoinColumn(name = "parent_id")
	private Category parent;

	@OneToMany(mappedBy = "parent")
	private Set<Category> children = new HashSet<>();

	
	
	
	
	public Category() {}
	
	
	public Category(String name) {
		this.name = name;
		this.alias = name;
		this.image = "default-category.png";
	}

	public Category(String name, Category parent) {
		this(name);
		this.parent = parent;
	}

	public static Category copyIdAndName(Category catgory) {
		Category copyCategory = new Category();
		copyCategory.setName(catgory.getName());
		copyCategory.setId(catgory.getId());
		return copyCategory;
	}
	
	public static Category copyIdAndName(Category catgory,String name) {
		Category copyCategory = new Category();
		copyCategory.setName(name);
		copyCategory.setId(catgory.getId());
		return copyCategory;
		
	}
	
	public static Category copyFull(Category catgory) {
		Category copyCategory = new Category();
		copyCategory.setName(catgory.getName());
		copyCategory.setId(catgory.getId());
		copyCategory.setAlias(catgory.getAlias());
		copyCategory.setImage(catgory.getImage());
		copyCategory.setEnabled(catgory.isEnabled());
		
		return copyCategory;
		
	}
	
	public static Category copyFull(Category catgory,String name) {
		Category copyCategory = Category.copyFull(catgory);
		copyCategory.setName(name);
		return copyCategory;
	}
	
	public Category(int id) {
		this.id = id;
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

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Category getParent() {
		return parent;
	}

	public void setParent(Category parent) {
		this.parent = parent;
	}

	public Set<Category> getChildren() {
		return children;
	}

	public void setChildren(Set<Category> children) {
		this.children = children;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Transient
	public String getImagePath() {
		return "/category-images/" + this.id + "/" + this.image;
	}
	
}
