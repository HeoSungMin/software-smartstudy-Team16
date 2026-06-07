package com.studyapp.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;

@Data
@Document(collection = "review_schedules")
public class ReviewSchedule {
    @Id
    private String id;
    private String userId;
    private String scheduleId; // 원본 수업 ID
    private String subjectName;
    private LocalDate reviewDate; // 복습 예정일
    private int stage; // 1~4단계
    private boolean completed; // 완료 여부
    private boolean notificationSent; // 알림 발송 여부
}