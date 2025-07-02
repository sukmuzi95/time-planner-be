package com.timeplanner.dev.domain.schedule.service;

import com.timeplanner.dev.domain.schedule.dto.request.RepeatOptionRequest;
import com.timeplanner.dev.domain.schedule.dto.request.ScheduleRequest;
import com.timeplanner.dev.domain.schedule.dto.response.ScheduleResponse;
import com.timeplanner.dev.domain.schedule.entity.RepeatOption;
import com.timeplanner.dev.domain.schedule.entity.RepeatType;
import com.timeplanner.dev.domain.schedule.entity.Schedule;
import com.timeplanner.dev.domain.schedule.repository.ScheduleRepository;
import com.timeplanner.dev.domain.user.entity.User;
import com.timeplanner.dev.domain.user.repository.UserRepository;
import com.timeplanner.dev.global.exception.ApiException;
import com.timeplanner.dev.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    @Override
    public void createSchedule(ScheduleRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ApiException.builder()
                        .errorCode(ErrorCode.NOT_FOUND_USER)
                        .build());

        RepeatOptionRequest ro = request.repeatOption();
        RepeatOption repeatOption = null;

        if (ro != null && ro.type() != RepeatType.NONE) {
            repeatOption = RepeatOption.builder()
                    .type(ro.type())
                    .interval(ro.interval())
                    .untilDate(ro.untilDate())
                    .build();
        }

        Schedule schedule = Schedule.builder()
                .title(request.title())
                .startDatetime(request.start())
                .endDatetime(request.end())
                .repeatOption(repeatOption)
                .owner(user)
                .build();

        scheduleRepository.save(schedule);
    }

    @Override
    public List<ScheduleResponse> getSchedulesByUserId(Long userId) {
        return scheduleRepository.findAllByOwnerId(userId).stream()
                .map(ScheduleResponse::from)
                .toList();
    }
}
