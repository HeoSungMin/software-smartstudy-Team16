package com.studyapp.service;

import com.studyapp.model.Event;
import com.studyapp.model.ReviewSchedule;
// import com.studyapp.model.User; // 유저 모델 import 필요
import com.studyapp.repository.EventRepository;
import com.studyapp.repository.ReviewScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final NotificationService notificationService;
    private final ReviewScheduleRepository reviewScheduleRepository;
    private final EventRepository eventRepository;
    private final UserService userService; // 이메일 주소를 가져오기 위해 주입

    @Scheduled(cron = "${notification.cron:0 0 8 * * *}")
    public void executeDailyNotifications() {
        LocalDate today = LocalDate.now();
        log.info("==== 일일 알림 스케줄러 실행 시작 (기준일: {}) ====", today);

        // 1. 에빙하우스 복습 알림 처리
        List<ReviewSchedule> todaysReviews = reviewScheduleRepository.findByReviewDateAndNotificationSentFalse(today);
        for (ReviewSchedule review : todaysReviews) {
            try {
                // 현준님의 UserService 구현에 맞춰 이메일을 가져오는 메서드로 수정이 필요할 수 있습니다.
                String userEmail = userService.findById(review.getUserId()).getEmail(); 
                notificationService.sendReviewNotification(review, userEmail);
                
                review.setNotificationSent(true);
                reviewScheduleRepository.save(review);
            } catch (Exception e) {
                log.error("복습 알림 처리 중 오류 발생: {}", e.getMessage());
            }
        }

        // 2. D-Day 알림 처리 (모든 진행 중인 이벤트를 가져와서 D-Day 계산)
        // ※ EventRepository에 findByCompletedFalse 쿼리가 있다고 가정합니다.
        List<Event> activeEvents = eventRepository.findAll(); 
       for (Event event : activeEvents) {
        if (!event.isCompleted() && event.isNotificationEnabled() && event.getNotifyBefore() != null) {
            long dDay = ChronoUnit.DAYS.between(today, event.getDueDate());
            
            // 설정한 알림일(예: 7일 전, 3일 전)과 일치하는지 확인
            if (event.getNotifyBefore().contains((int) dDay)) {
                try {
                    // ★ 추가된 부분: 오늘 이미 알림을 보냈는지 확인 (중복 발송 방지)
                    // 만약 Event 모델에 lastNotifiedDate가 있다면 활용하세요. 
                    // 없다면, 이벤트가 오늘 알림을 보낼 조건에 맞는지 한 번 더 체크하는 것으로 충분합니다.
                    
                    String userEmail = userService.findById(event.getUserId()).getEmail();
                    notificationService.sendDdayNotification(event, userEmail, (int) dDay);
                    
                    log.info("D-Day 알림 발송 완료: 이벤트 ID {}", event.getId());
                } catch (Exception e) {
                    log.error("D-Day 알림 처리 중 오류 발생: {}", e.getMessage());
                }
            }
        }
    }
        log.info("==== 일일 알림 스케줄러 실행 완료 ====");
    }
}