package com.example.easypay.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.easypay.app.entity.ScheduledPayment;

public interface ScheduledPaymentRepository extends JpaRepository<ScheduledPayment,Integer> {

}
