package com.planApiService.manage.controller;

import com.planApiService.manage.dto.request.ScheduleCreateRequest;
import com.planApiService.manage.dto.response.ScheduleResponse;
import com.planApiService.manage.dto.response.UserResponse;
import com.planApiService.manage.service.ScheduleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final HttpSession session; //비즈니스 로직은 서비스 레이어에서 체크

    @PostMapping
    public void createSchedule(@RequestBody @Valid ScheduleCreateRequest request, @RequestHeader("Authorization") String token) {
        scheduleService.createSchedule(request, token);
    }

    @GetMapping
    public List<ScheduleResponse> getSchedules() {
        return scheduleService.getAllSchedules();
    }

    @GetMapping("/{id}")
    public ScheduleResponse getScheduleById(@PathVariable Long id) {
        return scheduleService.getScheduleById(id);
    }

    @PutMapping("/{id}")
    public void updateSchedule(@PathVariable Long id, @RequestBody @Valid ScheduleCreateRequest request) {
        scheduleService.updateSchedule(id, request, session);
    }

    @DeleteMapping("/{id}")
    public void deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id, session);
    }
}
