package com.example.easypay.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.easypay.app.entity.UserSession;

public interface UserSessionRepository extends JpaRepository<UserSession,Integer> {

}
