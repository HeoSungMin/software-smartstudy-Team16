package com.studyapp.controller;

import com.studyapp.model.User;
import com.studyapp.service.EventService;
import com.studyapp.service.ScheduleService;
import com.studyapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final UserService userService;
    private final ScheduleService scheduleService;
    private final EventService eventService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("schedules", scheduleService.getSchedulesByUser(user.getId()));
        model.addAttribute("eventsWithDday", eventService.getEventsWithDday(user.getId()));
        return "dashboard/index";
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/dashboard";
    }
}
