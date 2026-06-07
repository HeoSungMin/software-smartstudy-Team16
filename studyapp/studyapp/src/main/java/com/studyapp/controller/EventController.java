package com.studyapp.controller;

import com.studyapp.model.Event;
import com.studyapp.model.User;
import com.studyapp.service.EventService;
import com.studyapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.Map;

@Controller
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final UserService userService;

    @GetMapping
    public String page(@AuthenticationPrincipal UserDetails ud, Model model) {
        User user = userService.findByUsername(ud.getUsername());
        model.addAttribute("eventsWithDday", eventService.getWithDday(user.getId()));
        model.addAttribute("allEvents", eventService.getAll(user.getId()));
        return "schedule/events";
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<?> add(@AuthenticationPrincipal UserDetails ud, @RequestBody Map<String, Object> body) {
        User user = userService.findByUsername(ud.getUsername());
        Event e = new Event();
        e.setUserId(user.getId());
        e.setTitle((String) body.get("title"));
        e.setType((String) body.get("type"));
        e.setSubjectName((String) body.getOrDefault("subjectName", ""));
        e.setDueDate(LocalDate.parse((String) body.get("dueDate")));
        e.setDescription((String) body.getOrDefault("description", ""));
        return ResponseEntity.ok(eventService.save(e));
    }

    @PostMapping("/{id}/toggle")
    @ResponseBody
    public ResponseEntity<?> toggle(@AuthenticationPrincipal UserDetails ud, @PathVariable String id) {
        User user = userService.findByUsername(ud.getUsername());
        eventService.toggleComplete(id, user.getId());
        return ResponseEntity.ok(Map.of("message", "변경되었습니다."));
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> delete(@PathVariable String id) {
        eventService.delete(id);
        return ResponseEntity.ok(Map.of("message", "삭제되었습니다."));
    }
}
