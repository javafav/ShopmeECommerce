package com.shopme.common.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "settings")
public class Setting {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name= "`key`",nullable = false, length = 128)
	private String key;
	
	@Column(nullable = false, length = 1024)
	private String value;
	
	@Enumerated
	@Column(length = 45,nullable = false)
	private SettingCategory category;

	public Setting() {
		
	}
	
	
	
	
	
	public Setting(String key, String value, SettingCategory category) {
		
		this.key = key;
		this.value = value;
		this.category = category;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public SettingCategory getCategory() {
		return category;
	}

	public void setCategory(SettingCategory category) {
		this.category = category;
	}



}
