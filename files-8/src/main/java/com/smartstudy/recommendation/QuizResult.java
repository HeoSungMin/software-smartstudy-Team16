package com.smartstudy.recommendation;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * F10, F11 - 퀴즈 응시 결과 도메인
 * MongoDB quizResults 컬렉션에 저장
 */
@Data
@Builder
@Document(collection = "quizResults")
@CompoundIndex(def = "{'userId': 1, 'subjectName': 1}")
public class QuizResult {

    @Id
    private String id;

    private String userId;
    private String subjectId;
    private String subjectName;

    private int totalQuestions;   // 총 문제 수
    private int correctCount;     // 정답 수

    private LocalDateTime takenAt;

    /** 정답률 계산 (0~100) */
    public double getAccuracyRate() {
        if (totalQuestions == 0) return 0.0;
        return (double) correctCount / totalQuestions * 100.0;
    }
}
