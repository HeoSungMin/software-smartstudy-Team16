package com.studyapp.controller;

import com.studyapp.model.User;
import com.studyapp.repository.ReviewScheduleRepository;
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
    private final ReviewScheduleRepository reviewScheduleRepository;

    @GetMapping({"/dashboard", "/"})
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) return "redirect:/auth/login";
        try {
            User user = userService.findByUsername(userDetails.getUsername());
            model.addAttribute("user", user);
            model.addAttribute("schedules", scheduleService.getByUser(user.getId()));
            model.addAttribute("eventsWithDday", eventService.getWithDday(user.getId()));
            model.addAttribute("todaysReviews", reviewScheduleRepository.findByUserId(user.getId()));
        } catch (Exception e) {
            return "redirect:/auth/login";
        }
        return "dashboard/index";
    }
}
