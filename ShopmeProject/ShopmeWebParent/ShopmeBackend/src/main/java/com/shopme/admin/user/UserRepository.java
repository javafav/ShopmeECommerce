package com.shopme.admin.user;

import javax.persistence.Entity;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopme.common.entity.User;

@Entity
public interface UserRepository extends JpaRepository<User, Integer> {

}
