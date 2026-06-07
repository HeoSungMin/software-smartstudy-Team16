package com.studyapp.service;

import com.studyapp.model.Event;
import com.studyapp.model.ReviewSchedule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // 로그 기록을 위해 추가
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j // 로그 기록용 어노테이션
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender mailSender;

    // 1. 에빙하우스 복습 알림 발송 메서드
    public void sendReviewNotification(ReviewSchedule review, String userEmail) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(userEmail);
            message.setSubject("[StudyApp] 복습 알림: " + review.getSubjectName());
            message.setText("오늘 " + review.getStage() + "단계 복습 예정일입니다. 잊지 말고 학습하세요!");
            
            mailSender.send(message);
            log.info("복습 알림 발송 성공: {}에게 발송됨", userEmail);
        } catch (Exception e) {
            log.error("복습 알림 발송 실패: {}", e.getMessage());
        }
    }

    // 2. D-Day 알림 발송 메서드
    public void sendDdayNotification(Event event, String userEmail, int dDay) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(userEmail);
            message.setSubject("[StudyApp] D-" + dDay + " 알림: " + event.getTitle());
            message.setText("마감일이 " + dDay + "일 남았습니다: " + event.getTitle());
            
            mailSender.send(message);
            log.info("D-Day 알림 발송 성공: {}에게 발송됨", userEmail);
        } catch (Exception e) {
            log.error("D-Day 알림 발송 실패: {}", e.getMessage());
        }
    }
}