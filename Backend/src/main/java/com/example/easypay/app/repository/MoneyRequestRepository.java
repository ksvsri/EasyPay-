package com.example.easypay.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.easypay.app.entity.MoneyRequest;

public interface MoneyRequestRepository extends JpaRepository<MoneyRequest,Integer> {

}
