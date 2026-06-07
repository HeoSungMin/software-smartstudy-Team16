package com.studyapp.repository;

import com.studyapp.model.ReviewSchedule;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.LocalDate;
import java.util.List;

public interface ReviewScheduleRepository extends MongoRepository<ReviewSchedule, String> {
    // 특정 날짜에 알림이 아직 안 나간 복습 일정 조회 (스케줄러용)
    List<ReviewSchedule> findByReviewDateAndNotificationSentFalse(LocalDate date);
    
    // 특정 유저의 특정 날짜 복습 일정 조회 (대시보드 표시용)
    List<ReviewSchedule> findByUserIdAndReviewDate(String userId, LocalDate date);
}