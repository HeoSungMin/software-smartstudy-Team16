package com.studyapp.dto;

import java.util.List;

// ── 내 순위 요약 ──
public record RankingResult(
    int    rank,
    String major,
    int    totalMinutes
) {}


// ── 레이더 차트용 (과목별 정답률) ──
record SubjectStatsResult(
    List<String> labels,
    List<Double> values
) {}


// ── 막대 차트용 (과목별 공부 시간) ──
record BarChartResult(
    List<String>  labels,
    List<Integer> values
) {}


// ── 통계 페이지 전체 응답 (API 단일 호출용) ──
public record StatisticsResult(
    List<String>  subjects,
    List<Double>  radarData,
    List<String>  barLabels,
    List<Integer> weekData,
    List<Integer> monthData,
    List<String>  trendLabels,
    List<Integer> trendAll
) {}
