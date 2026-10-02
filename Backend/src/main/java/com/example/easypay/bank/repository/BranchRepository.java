package com.example.easypay.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.easypay.bank.entity.Branch;

@Repository
public interface BranchRepository extends JpaRepository<Branch,Integer> {

}
