package com.timeplanner.dev.domain.schedule.dto.request;

import com.timeplanner.dev.domain.schedule.entity.RepeatType;

import java.time.LocalDateTime;

public record RepeatOptionRequest(
        RepeatType type,
        int interval,
        LocalDateTime untilDate
){}
