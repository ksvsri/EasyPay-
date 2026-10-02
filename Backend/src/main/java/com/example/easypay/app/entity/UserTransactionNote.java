package com.example.easypay.app.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@Table(
    name = "user_transaction_notes",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"app_user_id", "transaction_id"})
    }
)

public class UserTransactionNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_transaction_note_id")
    private Integer userTransactionNoteId;

    // Who wrote the note
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_user_id", nullable = false)
    private AppUser appUser;

    // Which transaction this note belongs to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private AppTransaction transaction;

    @Column(name = "note_text", nullable = false, columnDefinition = "TEXT")
    private String noteText;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

