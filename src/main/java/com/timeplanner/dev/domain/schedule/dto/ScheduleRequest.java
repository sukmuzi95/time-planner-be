package com.timeplanner.dev.domain.schedule.dto;

import java.time.LocalDateTime;

public record ScheduleRequest(
        String title,
        LocalDateTime start,
        LocalDateTime end,
        RepeatOptionRequest repeatOption
) {}

