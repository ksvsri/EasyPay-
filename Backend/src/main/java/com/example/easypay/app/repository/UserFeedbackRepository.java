package com.example.easypay.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.easypay.app.entity.UserFeedback;

public interface UserFeedbackRepository extends JpaRepository<UserFeedback,Integer> {

}
