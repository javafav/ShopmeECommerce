package com.shopme.admin.user;

import javax.persistence.Entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shopme.common.entity.User;

@Entity
public interface UserRepository extends JpaRepository<User, Integer> {

	@Query("SELECT u FROM User u WHERE u.email = :email")
	public User getUserByName(@Param("email") String email);
	
	public Long countById(Integer id);
	
	@Query("UPDATE User u SET u.enabled = ?2  WHERE u.id = ?1")
	@Modifying
	public void updateEnabledStatus(Integer id, boolean status);
}
