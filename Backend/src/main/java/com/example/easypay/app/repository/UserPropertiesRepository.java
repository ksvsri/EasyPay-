package com.example.easypay.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.easypay.app.entity.UserProperties;

public interface UserPropertiesRepository extends JpaRepository<UserProperties,Integer> {
	
	

}
