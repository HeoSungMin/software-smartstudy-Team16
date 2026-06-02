package com.smartstudy.service;

import com.smartstudy.domain.ReviewSchedule;
import com.smartstudy.repository.ReviewScheduleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReviewScheduler {

    private final ReviewScheduleRepository reviewScheduleRepository;

    public ReviewScheduler(ReviewScheduleRepository reviewScheduleRepository) {
        this.reviewScheduleRepository = reviewScheduleRepository;
    }

    // 에빙하우스 망각 곡선: 1일, 7일, 30일 후 복습 일정 생성
    public ReviewSchedule schedule(String userId, String noteId) {
        LocalDateTime now = LocalDateTime.now();

        ReviewSchedule schedule = new ReviewSchedule();
        schedule.setUserId(userId);
        schedule.setNoteId(noteId);
        schedule.setReview1Day(now.plusDays(1));
        schedule.setReview7Days(now.plusDays(7));
        schedule.setReview30Days(now.plusDays(30));
        schedule.setStatus("pending");
        schedule.setCreatedAt(now);

        return reviewScheduleRepository.save(schedule);
    }
}
