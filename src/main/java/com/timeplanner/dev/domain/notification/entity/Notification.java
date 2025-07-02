package com.timeplanner.dev.domain.notification.entity;

import com.timeplanner.dev.domain.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Notification {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User receiver;

    private String content;
    private boolean read;

    private LocalDateTime createdAt;
}
