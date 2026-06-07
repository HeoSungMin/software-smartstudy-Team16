package com.studyapp.controller;

import com.studyapp.model.User;
import com.studyapp.repository.UserRepository;
import com.studyapp.repository.ReviewScheduleRepository; // 1. import 추가
import com.studyapp.service.EventService;
import com.studyapp.service.ScheduleService;
import com.studyapp.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.time.LocalDate; // 2. import 추가

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final ScheduleService scheduleService;
    private final EventService eventService;
    private final ReviewScheduleRepository reviewScheduleRepository; // 3. 의존성 주입

    @GetMapping({"/dashboard", "/"})
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails,
                            @AuthenticationPrincipal OAuth2User oAuth2User,
                            HttpSession session,
                            Model model) {
        User user = resolveCurrentUser(userDetails, oAuth2User, session);
        if (user == null) return "redirect:/auth/login";

        model.addAttribute("user", user);
        model.addAttribute("schedules", scheduleService.getByUser(user.getId()));
        model.addAttribute("eventsWithDday", eventService.getWithDday(user.getId()));
        
        // 4. 여기에 추가
        model.addAttribute("todaysReviews", reviewScheduleRepository.findByUserIdAndReviewDate(user.getId(), LocalDate.now()));

        return "dashboard/index";
    }

    private User resolveCurrentUser(UserDetails ud, OAuth2User ou, HttpSession session) {
        if (ud != null) {
            try { return userService.findByUsername(ud.getUsername()); } catch (Exception e) { return null; }
        }
        String socialUserId = (String) session.getAttribute("socialUserId");
        if (socialUserId != null) {
            return userRepository.findById(socialUserId).orElse(null);
        }
        return null;
    }
}