package com.timeplanner.dev.domain.schedule.entity;

import jakarta.persistence.*;
import lombok.*;

import java.net.Proxy;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "repeat_option")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class RepeatOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "repeat_type")
    private RepeatType type; // NONE, DAILY, WEEKLY, MONTHLY

    @Column(name = "repeat_interval")
    private int interval;

    private LocalDateTime untilDate;

    @OneToMany(mappedBy = "repeatOption")
    private List<Schedule> schedules = new ArrayList<>();
}
