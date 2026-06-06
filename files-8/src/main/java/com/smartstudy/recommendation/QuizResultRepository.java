package com.smartstudy.recommendation;

import com.smartstudy.recommendation.QuizResult;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * F10, F11 - 퀴즈 결과 레포지토리
 */
public interface QuizResultRepository extends MongoRepository<QuizResult, String> {

    // 특정 사용자의 전체 퀴즈 결과 조회
    List<QuizResult> findByUserId(String userId);

    // 특정 사용자 + 과목의 퀴즈 결과 조회
    List<QuizResult> findByUserIdAndSubjectName(String userId, String subjectName);

    // 최근 N개 결과 (대시보드 최근 기록용)
    List<QuizResult> findTop5ByUserIdOrderByTakenAtDesc(String userId);
}
