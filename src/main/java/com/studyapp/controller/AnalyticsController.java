package com.studyapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.studyapp.dto.*;
import com.studyapp.model.Subject;
import com.studyapp.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    // ── F-13: 히트맵 데이터 ──────────────────────────
    // GET /api/analytics/heatmap?days=365
    // 응답: { "2026-05-01": 120, "2026-05-02": 90, ... }
    @GetMapping("/heatmap")
    public ResponseEntity<Map<String, Integer>> getHeatmap(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(defaultValue = "365") int days
    ) {
        String userId = analyticsService.getUserIdByUsername(user.getUsername());
        return ResponseEntity.ok(analyticsService.getHeatmapData(userId, days));
    }

    // ── F-13: 오늘 공부 시간 + 연속 학습일 ────────────
    // GET /api/analytics/activity-summary
    // 응답: { "todayMinutes": 120, "streakDays": 7 }
    @GetMapping("/activity-summary")
    public ResponseEntity<Map<String, Integer>> getActivitySummary(
            @AuthenticationPrincipal UserDetails user
    ) {
        String userId = analyticsService.getUserIdByUsername(user.getUsername());
        return ResponseEntity.ok(Map.of(
                "todayMinutes", analyticsService.getTodayMinutes(userId),
                "streakDays",   analyticsService.getStreakDays(userId)
        ));
    }

    // ── F-14: 통계 전체 (차트 데이터 한 번에) ────────
    // GET /api/analytics/statistics
    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResult> getStatistics(
            @AuthenticationPrincipal UserDetails user
    ) {
        String userId = analyticsService.getUserIdByUsername(user.getUsername());
        return ResponseEntity.ok(analyticsService.getStatistics(userId));
    }

    // ── F-10: 오답 목록 ──────────────────────────────
    // GET /api/analytics/wrong-answers?subjectId=xxx
    @GetMapping("/wrong-answers")
    public ResponseEntity<List<WrongAnswerItem>> getWrongAnswers(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(required = false) String subjectId
    ) {
        String userId = analyticsService.getUserIdByUsername(user.getUsername());
        return ResponseEntity.ok(analyticsService.getWrongAnswers(userId, subjectId));
    }

    // ── 과목 목록 (오답 필터 드롭다운용) ─────────────
    // GET /api/analytics/subjects
    @GetMapping("/subjects")
    public ResponseEntity<List<Subject>> getSubjects(
            @AuthenticationPrincipal UserDetails user
    ) {
        String userId = analyticsService.getUserIdByUsername(user.getUsername());
        return ResponseEntity.ok(analyticsService.getSubjectsByUser(userId));
    }

    // ── F-15: 소셜 랭킹 ──────────────────────────────
    // GET /api/analytics/ranking?scope=all  (all | major)
    @GetMapping("/ranking")
    public ResponseEntity<List<RankingEntry>> getRanking(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(defaultValue = "all") String scope
    ) {
        String userId = analyticsService.getUserIdByUsername(user.getUsername());
        return ResponseEntity.ok(analyticsService.getRankings(userId, scope, 20));
    }

    // ── F-15: 내 순위 ────────────────────────────────
    // GET /api/analytics/my-rank?scope=all
    @GetMapping("/my-rank")
    public ResponseEntity<RankingResult> getMyRank(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(defaultValue = "all") String scope
    ) {
        String userId = analyticsService.getUserIdByUsername(user.getUsername());
        RankingResult result = analyticsService.getMyRank(userId, scope);
        return result != null
                ? ResponseEntity.ok(result)
                : ResponseEntity.notFound().build();
    }
}
