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
    public String eventsPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("eventsWithDday", eventService.getEventsWithDday(user.getId()));
        model.addAttribute("allEvents", eventService.getAllEvents(user.getId()));
        return "schedule/events";
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<?> addEvent(@AuthenticationPrincipal UserDetails userDetails,
                                       @RequestBody Map<String, Object> body) {
        User user = userService.findByUsername(userDetails.getUsername());
        Event event = new Event();
        event.setUserId(user.getId());
        event.setTitle((String) body.get("title"));
        event.setType((String) body.get("type"));
        event.setSubjectName((String) body.getOrDefault("subjectName", ""));
        event.setDueDate(LocalDate.parse((String) body.get("dueDate")));
        event.setDescription((String) body.getOrDefault("description", ""));
        return ResponseEntity.ok(eventService.save(event));
    }

    @PostMapping("/{id}/toggle")
    @ResponseBody
    public ResponseEntity<?> toggleComplete(@AuthenticationPrincipal UserDetails userDetails,
                                             @PathVariable String id) {
        User user = userService.findByUsername(userDetails.getUsername());
        eventService.toggleComplete(id, user.getId());
        return ResponseEntity.ok(Map.of("message", "상태가 변경되었습니다."));
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteEvent(@PathVariable String id) {
        eventService.delete(id);
        return ResponseEntity.ok(Map.of("message", "삭제되었습니다."));
    }
}
