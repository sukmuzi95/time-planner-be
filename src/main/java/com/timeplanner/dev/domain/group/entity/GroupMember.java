package com.timeplanner.dev.domain.group.entity;

import com.timeplanner.dev.domain.user.entity.User;
import jakarta.persistence.*;

@Entity
public class GroupMember {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String role; // "OWNER", "MEMBER"
}
