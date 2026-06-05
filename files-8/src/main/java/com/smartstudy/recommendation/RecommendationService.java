package com.smartstudy.recommendation;

import com.smartstudy.recommendation.QuizResult;
import com.smartstudy.recommendation.SubjectRecommendationDto;
import com.smartstudy.recommendation.QuizResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * F11 - 학습 추천 서비스
 *
 * 퀴즈 정답률이 낮은 취약 과목을 자동 감지하여
 * 대시보드에 재학습 권장 메시지를 제공한다.
 * (요구사항 명세서 F11 / 설계서 AnalysisSystem 참조)
 */
@Service
@RequiredArgsConstructor
public class RecommendationService {

    // 취약 과목 기준: 정답률 60% 미만
    private static final double WEAK_THRESHOLD = 60.0;

    private final QuizResultRepository quizResultRepository;

    // ──────────────────────────────────────────────
    //  전체 과목별 분석 (정답률 오름차순 정렬)
    // ──────────────────────────────────────────────

    /**
     * 사용자의 모든 과목을 정답률 오름차순으로 반환한다.
     * Thymeleaf 뷰의 "과목별 정답률" 섹션에서 사용.
     */
    public List<SubjectRecommendationDto> getAllSubjectStats(String userId) {
        List<QuizResult> results = quizResultRepository.findByUserId(userId);

        // 과목별 그룹핑 → 평균 정답률 집계
        Map<String, DoubleSummaryStatistics> statsMap = results.stream()
                .collect(Collectors.groupingBy(
                        QuizResult::getSubjectName,
                        Collectors.summarizingDouble(QuizResult::getAccuracyRate)
                ));

        return statsMap.entrySet().stream()
                .map(e -> {
                    double avg = Math.round(e.getValue().getAverage() * 10.0) / 10.0;
                    return new SubjectRecommendationDto(
                            e.getKey(),
                            avg,
                            e.getValue().getCount(),
                            avg < WEAK_THRESHOLD,
                            SubjectRecommendationDto.resolveLevel(avg)
                    );
                })
                // 정답률 낮은 순 정렬 (취약 과목이 상단에)
                .sorted(Comparator.comparingDouble(SubjectRecommendationDto::averageAccuracy))
                .collect(Collectors.toList());
    }

    // ──────────────────────────────────────────────
    //  취약 과목만 필터링 (넛지 배너용)
    // ──────────────────────────────────────────────

    /**
     * F11 핵심: 정답률 60% 미만 취약 과목만 반환.
     * 대시보드 상단 경고 배너 및 사이드바 뱃지 카운트에 사용.
     */
    public List<SubjectRecommendationDto> getWeakSubjects(String userId) {
        return getAllSubjectStats(userId).stream()
                .filter(SubjectRecommendationDto::isWeak)
                .collect(Collectors.toList());
    }

    // ──────────────────────────────────────────────
    //  요약 통계 (stat 카드 4개용)
    // ──────────────────────────────────────────────

    public record SummaryStats(
            int totalSubjects,        // 전체 과목 수
            int weakSubjectCount,     // 취약 과목 수
            double overallAvgAccuracy,// 전체 평균 정답률
            long totalQuizCount       // 총 퀴즈 응시 횟수
    ) {}

    /**
     * 대시보드 상단 4개 stat 카드 데이터 반환
     */
    public SummaryStats getSummaryStats(String userId) {
        List<SubjectRecommendationDto> all = getAllSubjectStats(userId);

        int totalSubjects   = all.size();
        int weakCount       = (int) all.stream().filter(SubjectRecommendationDto::isWeak).count();
        long totalQuizCount = all.stream().mapToLong(SubjectRecommendationDto::quizCount).sum();
        double overallAvg   = all.stream()
                .mapToDouble(SubjectRecommendationDto::averageAccuracy)
                .average()
                .orElse(0.0);
        overallAvg = Math.round(overallAvg * 10.0) / 10.0;

        return new SummaryStats(totalSubjects, weakCount, overallAvg, totalQuizCount);
    }
}
