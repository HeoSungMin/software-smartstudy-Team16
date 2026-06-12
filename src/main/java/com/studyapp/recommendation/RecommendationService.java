package com.studyapp.recommendation;

import com.studyapp.model.QuizResult;
import com.studyapp.repository.QuizResultRepository;
import com.studyapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private static final double WEAK_THRESHOLD = 60.0;

    // RecommendationQuizResult 대신 실제 QuizResult 사용
    private final QuizResultRepository quizResultRepository;
    private final UserRepository       userRepository;

    // username → userId 변환
    private String resolveUserId(String usernameOrId) {
        // 이미 userId 형태면 그대로 사용, username이면 변환
        return userRepository.findByUsername(usernameOrId)
                .map(u -> u.getId())
                .orElse(usernameOrId); // 못 찾으면 그대로 (이미 userId일 수 있음)
    }

    public List<SubjectRecommendationDto> getAllSubjectStats(String usernameOrId) {
        String userId = resolveUserId(usernameOrId);
        List<QuizResult> results = quizResultRepository.findByUserIdOrderBySolvedAtAsc(userId);

        if (results.isEmpty()) return List.of();

        // subjectId 기준으로 그룹화 후 정답률 계산
        Map<String, List<QuizResult>> bySubject = results.stream()
                .collect(Collectors.groupingBy(
                        qr -> qr.getSubjectId() != null ? qr.getSubjectId() : "기타"
                ));

        return bySubject.entrySet().stream()
                .map(e -> {
                    List<QuizResult> subjectResults = e.getValue();
                    long totalQ   = subjectResults.stream().mapToLong(QuizResult::getTotalCount).sum();
                    long correctQ = subjectResults.stream().mapToLong(QuizResult::getCorrectCount).sum();
                    double accuracy = totalQ > 0
                            ? Math.round((double) correctQ / totalQ * 1000.0) / 10.0
                            : 0.0;
                    long quizCount = subjectResults.size();

                    return new SubjectRecommendationDto(
                            e.getKey(),
                            accuracy,
                            quizCount,
                            accuracy < WEAK_THRESHOLD,
                            SubjectRecommendationDto.resolveLevel(accuracy)
                    );
                })
                .sorted(Comparator.comparingDouble(SubjectRecommendationDto::averageAccuracy))
                .collect(Collectors.toList());
    }

    public List<SubjectRecommendationDto> getWeakSubjects(String usernameOrId) {
        return getAllSubjectStats(usernameOrId).stream()
                .filter(SubjectRecommendationDto::isWeak)
                .collect(Collectors.toList());
    }

    public record SummaryStats(
            int totalSubjects,
            int weakSubjectCount,
            double overallAvgAccuracy,
            long totalQuizCount
    ) {}

    public SummaryStats getSummaryStats(String usernameOrId) {
        List<SubjectRecommendationDto> all = getAllSubjectStats(usernameOrId);
        int    totalSubjects  = all.size();
        int    weakCount      = (int) all.stream().filter(SubjectRecommendationDto::isWeak).count();
        long   totalQuizCount = all.stream().mapToLong(SubjectRecommendationDto::quizCount).sum();
        double overallAvg     = all.isEmpty() ? 0.0
                : Math.round(all.stream()
                        .mapToDouble(SubjectRecommendationDto::averageAccuracy)
                        .average().orElse(0.0) * 10.0) / 10.0;
        return new SummaryStats(totalSubjects, weakCount, overallAvg, totalQuizCount);
    }
}
