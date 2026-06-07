package com.studyapp.service;

import com.studyapp.model.ReviewSchedule;
import com.studyapp.model.Schedule;
import com.studyapp.repository.ReviewScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewScheduleService {

    private final ReviewScheduleRepository reviewScheduleRepository;

    @Value("${notification.review.intervals:1,7,16,35}")
    private List<Integer> reviewIntervals;

    public void createReviewSchedules(Schedule schedule) {
        List<ReviewSchedule> reviewSchedules = new ArrayList<>();
        LocalDate baseDate = LocalDate.now(); 

        for (int i = 0; i < reviewIntervals.size(); i++) {
            ReviewSchedule review = new ReviewSchedule();
            review.setUserId(schedule.getUserId());
            review.setScheduleId(schedule.getId());
            review.setSubjectName(schedule.getSubjectName());
            review.setReviewDate(baseDate.plusDays(reviewIntervals.get(i)));
            review.setStage(i + 1);
            review.setCompleted(false);
            review.setNotificationSent(false);

            reviewSchedules.add(review);
        }

        reviewScheduleRepository.saveAll(reviewSchedules);
        log.info("에빙하우스 복습 일정 {}개 생성 완료 (과목: {})", reviewSchedules.size(), schedule.getSubjectName());
    }
}