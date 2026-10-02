package com.example.easypay.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.easypay.app.entity.UserTransactionNote;

public interface UserTransactionNoteRepository extends JpaRepository<UserTransactionNote,Integer> {

}
