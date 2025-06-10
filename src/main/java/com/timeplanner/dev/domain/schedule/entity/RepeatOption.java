package com.timeplanner.dev.domain.schedule.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "repeat_option")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RepeatOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RepeatType type; // NONE, DAILY, WEEKLY, MONTHLY

    private int interval;
    private LocalDateTime untilDate;

    @OneToMany(mappedBy = "repeatOption")
    private List<Schedule> schedules = new ArrayList<>();
}
