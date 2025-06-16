package com.timeplanner.dev.domain.schedule.dto;

import com.timeplanner.dev.domain.schedule.entity.RepeatType;

import java.time.LocalDateTime;

public record RepeatOptionRequest(
        RepeatType type,
        int interval,
        LocalDateTime untilDate
){}
