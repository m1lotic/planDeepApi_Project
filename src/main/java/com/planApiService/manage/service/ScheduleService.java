package com.planApiService.manage.service;

import com.planApiService.manage.dto.request.ScheduleCreateRequest;
import com.planApiService.manage.dto.response.ScheduleResponse;
import com.planApiService.manage.entity.Schedule;
import com.planApiService.manage.entity.User;
import com.planApiService.manage.jwt.JwtUtil;
import com.planApiService.manage.repository.ScheduleRepository;
import com.planApiService.manage.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) //트랜잭션 처리를 위해 분리
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public void createSchedule(ScheduleCreateRequest requestDto, String token) {
        // 로그인 유저 확인
        User user = userRepository.findByEmail(jwtUtil.getEmailFromToken(token))
                .orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));

        Schedule schedule = Schedule.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .user(user)
                .build();

        scheduleRepository.save(schedule);
    }

    @Transactional
    public List<ScheduleResponse> getAllSchedules() {
        return scheduleRepository.findAll().stream()
                .map(ScheduleResponse::new)
                .toList();
    }

    @Transactional
    public ScheduleResponse getScheduleById(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("일정을 찾을 수 없습니다."));
        return new ScheduleResponse(schedule);
    }

    @Transactional
    public void updateSchedule(Long id, ScheduleCreateRequest requestDto, HttpSession session) {
        String email = (String) session.getAttribute("loginEmail");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));

        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("일정을 찾을 수 없습니다."));

        // 본인인지 검증
        if (!schedule.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }

        schedule.update(requestDto.getTitle(), requestDto.getContent());
    }

    @Transactional
    public void deleteSchedule(Long id, HttpSession session) {
        String email = (String) session.getAttribute("loginEmail");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));

        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("일정을 찾을 수 없습니다."));

        if (!schedule.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }

        scheduleRepository.delete(schedule);
    }
}
