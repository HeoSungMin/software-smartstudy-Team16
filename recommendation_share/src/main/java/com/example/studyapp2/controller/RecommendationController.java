package com.example.studyapp2.controller;

import com.example.studyapp2.dto.SubjectStat;
import com.example.studyapp2.entity.User;
import com.example.studyapp2.service.RecommendationService;
import com.example.studyapp2.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final UserService userService;

    public RecommendationController(RecommendationService recommendationService,
                                    UserService userService) {
        this.recommendationService = recommendationService;
        this.userService = userService;
    }

    @GetMapping({"/", "/recommendation"})
    public String recommendationPage(Model model) {
        User user = userService.findByUsername("demo");
        List<SubjectStat> stats = recommendationService.getSubjectStats();

        model.addAttribute("user",              user);
        model.addAttribute("subjectStats",      stats);
        model.addAttribute("recommendations",   recommendationService.getRecommendations());
        model.addAttribute("recentHistory",     recommendationService.getRecentHistory());
        model.addAttribute("weakCount",         recommendationService.getWeakCount());
        model.addAttribute("totalSubjectCount", stats.size());
        model.addAttribute("avgAccuracy",       recommendationService.getAvgAccuracy());
        model.addAttribute("totalQuizCount",    "30회");
        model.addAttribute("activePage",        "recommendation");
        return "recommendation";
    }
}
