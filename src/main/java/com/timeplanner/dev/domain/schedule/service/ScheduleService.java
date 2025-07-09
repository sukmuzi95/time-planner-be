package com.timeplanner.dev.domain.schedule.service;

import com.timeplanner.dev.domain.schedule.dto.request.ScheduleRequest;
import com.timeplanner.dev.domain.schedule.dto.response.ScheduleResponse;

import java.util.List;

public interface ScheduleService {

    void createSchedule(ScheduleRequest request, Long userId);
    List<ScheduleResponse> getSchedulesByUserId(Long userId);
    List<ScheduleResponse> getAllSchedules();
    void deleteScheduleById(Long userId, Long scheduleId);
}
