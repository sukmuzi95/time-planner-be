package com.timeplanner.dev.domain.schedule.api;

import com.timeplanner.dev.domain.schedule.dto.request.ScheduleRequest;
import com.timeplanner.dev.domain.schedule.dto.response.ScheduleResponse;
import com.timeplanner.dev.domain.schedule.service.ScheduleService;
import com.timeplanner.dev.global.security.auth.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping("/shared")
    public ResponseEntity<Void> createSchedule(
            @RequestBody @Valid ScheduleRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        scheduleService.createSchedule(request, userDetails.getUser().getId());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/shared")
    public ResponseEntity<List<ScheduleResponse>> getAllSchedules(@AuthenticationPrincipal UserDetailsImpl userDetails) {
//        Long userId = userDetails.getUser().getId();
//        List<ScheduleResponse> schedules = scheduleService.getSchedulesByUserId(userId);
        List<ScheduleResponse> schedules = scheduleService.getAllSchedules();

        return ResponseEntity.ok(schedules);
    }

    @DeleteMapping("/shared/{id}")
    public ResponseEntity<Void> deleteSchedule(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
        Long userId = userDetails.getUser().getId();
        scheduleService.deleteScheduleById(userId, id);

        return ResponseEntity.ok().build();
    }
}
