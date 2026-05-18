package com.studyapp.controller;

import com.studyapp.model.Schedule;
import com.studyapp.model.User;
import com.studyapp.service.ScheduleService;
import com.studyapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final UserService userService;

    @GetMapping
    public String schedulePage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("schedules", scheduleService.getSchedulesByUser(user.getId()));
        model.addAttribute("userId", user.getId());
        return "schedule/index";
    }

    // 시간표 블록 추가
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<?> addSchedule(@AuthenticationPrincipal UserDetails userDetails,
                                          @RequestBody Map<String, Object> body) {
        User user = userService.findByUsername(userDetails.getUsername());
        Schedule schedule = new Schedule();
        schedule.setUserId(user.getId());
        schedule.setSubjectName((String) body.get("subjectName"));
        schedule.setColor((String) body.get("color"));
        schedule.setDayOfWeek((Integer) body.get("dayOfWeek"));
        schedule.setStartHour((Integer) body.get("startHour"));
        schedule.setStartMinute((Integer) body.get("startMinute"));
        schedule.setEndHour((Integer) body.get("endHour"));
        schedule.setEndMinute((Integer) body.get("endMinute"));
        schedule.setProfessor((String) body.getOrDefault("professor", ""));
        schedule.setRoom((String) body.getOrDefault("room", ""));
        return ResponseEntity.ok(scheduleService.save(schedule));
    }

    // 메모 수정 (F-4: 과목 클릭 시 메모 작성)
    @PostMapping("/{id}/memo")
    @ResponseBody
    public ResponseEntity<?> updateMemo(@AuthenticationPrincipal UserDetails userDetails,
                                         @PathVariable String id,
                                         @RequestBody Map<String, String> body) {
        User user = userService.findByUsername(userDetails.getUsername());
        Schedule updated = scheduleService.updateMemo(id, user.getId(), body.get("memo"));
        return ResponseEntity.ok(updated);
    }

    // 시간표 상세 조회 (블록 클릭 시)
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> getSchedule(@PathVariable String id) {
        return ResponseEntity.ok(scheduleService.findById(id));
    }

    // 시간표 삭제
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteSchedule(@AuthenticationPrincipal UserDetails userDetails,
                                             @PathVariable String id) {
        User user = userService.findByUsername(userDetails.getUsername());
        scheduleService.delete(id, user.getId());
        return ResponseEntity.ok(Map.of("message", "삭제되었습니다."));
    }
}
