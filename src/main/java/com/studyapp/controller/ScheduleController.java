package com.studyapp.controller;

import com.studyapp.model.CourseInfo;
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

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final UserService userService;

    @GetMapping
    public String page(@AuthenticationPrincipal UserDetails ud, Model model) {
        User user = userService.findByUsername(ud.getUsername());
        model.addAttribute("schedules", scheduleService.getByUser(user.getId()));
        model.addAttribute("user", user);
        return "schedule/index";
    }

    /** 단건 추가 (셀 클릭 또는 직접 추가) */
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<?> add(@AuthenticationPrincipal UserDetails ud,
                                  @RequestBody Map<String, Object> body) {
        User user = userService.findByUsername(ud.getUsername());
        Schedule s = new Schedule();
        s.setUserId(user.getId());
        s.setSubjectName((String) body.get("subjectName"));
        s.setColor((String) body.get("color"));
        s.setDayOfWeek((Integer) body.get("dayOfWeek"));
        s.setStartHour((Integer) body.get("startHour"));
        s.setStartMinute((Integer) body.get("startMinute"));
        s.setEndHour((Integer) body.get("endHour"));
        s.setEndMinute((Integer) body.get("endMinute"));
        s.setProfessor((String) body.getOrDefault("professor", ""));
        s.setRoom((String) body.getOrDefault("room", ""));
        return ResponseEntity.ok(scheduleService.save(s));
    }

    /** ★ 강의 검색 결과 일괄 등록 */
    @PostMapping("/bulk-add")
    @ResponseBody
    public ResponseEntity<?> bulkAdd(@AuthenticationPrincipal UserDetails ud,
                                      @RequestBody List<CourseInfo> courses) {
        User user = userService.findByUsername(ud.getUsername());
        List<Schedule> saved = scheduleService.bulkRegister(user.getId(), courses);
        return ResponseEntity.ok(Map.of(
                "count", saved.size(),
                "schedules", saved,
                "message", saved.size() + "개 강의가 시간표에 추가되었습니다."
        ));
    }

    /** 메모 저장 */
    @PostMapping("/{id}/memo")
    @ResponseBody
    public ResponseEntity<?> memo(@AuthenticationPrincipal UserDetails ud,
                                   @PathVariable String id,
                                   @RequestBody Map<String, String> body) {
        User user = userService.findByUsername(ud.getUsername());
        return ResponseEntity.ok(scheduleService.updateMemo(id, user.getId(), body.get("memo")));
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> get(@PathVariable String id) {
        return ResponseEntity.ok(scheduleService.findById(id));
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> delete(@AuthenticationPrincipal UserDetails ud, @PathVariable String id) {
        User user = userService.findByUsername(ud.getUsername());
        scheduleService.delete(id, user.getId());
        return ResponseEntity.ok(Map.of("message", "삭제되었습니다."));
    }
}
