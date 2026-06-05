package com.smartstudy.recommendation;

import com.smartstudy.recommendation.SubjectRecommendationDto;
import com.smartstudy.recommendation.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * F11 - 학습 추천 컨트롤러
 *
 * [GET]  /recommendation          → Thymeleaf 뷰 렌더링 (recommendation/index.html)
 * [GET]  /recommendation/api/all  → 전체 과목 통계 JSON (REST)
 * [GET]  /recommendation/api/weak → 취약 과목만 JSON (REST / 사이드바 뱃지 갱신용)
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/recommendation")
public class RecommendationController {

    private final RecommendationService recommendationService;

    // ──────────────────────────────────────────────
    //  Thymeleaf 뷰 렌더링
    // ──────────────────────────────────────────────

    /**
     * GET /recommendation
     * 학습 추천 페이지 전체 렌더링
     * - allSubjects   : 과목별 정답률 리스트 (bar 차트용)
     * - weakSubjects  : 취약 과목 리스트 (넛지 배너용)
     * - summary       : 4개 stat 카드용 요약 데이터
     * - hasWeak       : 배너 표시 여부
     */
    @GetMapping
    public String recommendationPage(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        String userId = (userDetails != null) ? userDetails.getUsername() : "guest";

        List<SubjectRecommendationDto> allSubjects  = recommendationService.getAllSubjectStats(userId);
        List<SubjectRecommendationDto> weakSubjects = recommendationService.getWeakSubjects(userId);
        RecommendationService.SummaryStats summary  = recommendationService.getSummaryStats(userId);

        model.addAttribute("allSubjects",  allSubjects);
        model.addAttribute("weakSubjects", weakSubjects);
        model.addAttribute("summary",      summary);
        model.addAttribute("hasWeak",      !weakSubjects.isEmpty());

        // 넛지 배너 과목명 문자열 (예: "자료구조, 컴퓨터네트워크")
        String weakNames = weakSubjects.stream()
                .map(SubjectRecommendationDto::subjectName)
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
        model.addAttribute("weakNames", weakNames);

        return "recommendation/index";
    }

    // ──────────────────────────────────────────────
    //  REST API (Vanilla JS fetch 호출용)
    // ──────────────────────────────────────────────

    /**
     * GET /recommendation/api/all
     * 전체 과목 통계 + summary 반환 (JSON)
     */
    @GetMapping("/api/all")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllStats(
            @AuthenticationPrincipal UserDetails userDetails) {

        String userId = (userDetails != null) ? userDetails.getUsername() : "guest";
        return ResponseEntity.ok(Map.of(
                "subjects", recommendationService.getAllSubjectStats(userId),
                "summary",  recommendationService.getSummaryStats(userId)
        ));
    }

    /**
     * GET /recommendation/api/weak
     * 취약 과목만 반환 (사이드바 뱃지 숫자 실시간 갱신용)
     */
    @GetMapping("/api/weak")
    @ResponseBody
    public ResponseEntity<List<SubjectRecommendationDto>> getWeakSubjects(
            @AuthenticationPrincipal UserDetails userDetails) {

        String userId = (userDetails != null) ? userDetails.getUsername() : "guest";
        return ResponseEntity.ok(recommendationService.getWeakSubjects(userId));
    }
}
