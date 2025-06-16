package com.timeplanner.dev.domain.schedule.service;

import com.timeplanner.dev.domain.schedule.dto.ScheduleRequest;
import com.timeplanner.dev.domain.schedule.dto.ScheduleResponse;

import java.util.List;

public interface ScheduleService {

    void createSchedule(ScheduleRequest request, Long userId);
    List<ScheduleResponse> getSchedulesByUserId(Long userId);
}
