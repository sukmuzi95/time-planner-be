package com.timeplanner.dev.domain.schedule.repository;

import com.timeplanner.dev.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findAllByUserId(Long userId);
}
