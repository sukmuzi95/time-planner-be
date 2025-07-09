package com.timeplanner.dev.domain.schedule.dto.response;

import com.timeplanner.dev.domain.schedule.entity.RepeatOption;
import com.timeplanner.dev.domain.schedule.entity.Schedule;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleResponse {
    private Long id;
    private String title;
    private Long ownerId;
    private String ownerName;
    private LocalDateTime start;
    private LocalDateTime end;
    private RepeatOptionResponse repeatOption;

    public static ScheduleResponse from(Schedule schedule) {
        return ScheduleResponse.builder()
                .id(schedule.getId())
                .title(schedule.getTitle())
                .ownerId(schedule.getOwner().getId())
                .ownerName(schedule.getOwner().getNickname())
                .start(schedule.getStartDatetime())
                .end(schedule.getEndDatetime())
                .repeatOption(schedule.getRepeatOption() != null
                        ? RepeatOptionResponse.from(schedule.getRepeatOption())
                        : null)
                .build();
    }

    @Getter
    @Builder
    public static class RepeatOptionResponse {
        private String type;
        private int interval;
        private LocalDateTime untilDate;

        public static RepeatOptionResponse from(RepeatOption repeatOption) {
            return RepeatOptionResponse.builder()
                    .type(repeatOption.getType().name())
                    .interval(repeatOption.getInterval())
                    .untilDate(repeatOption.getUntilDate())
                    .build();
        }
    }
}