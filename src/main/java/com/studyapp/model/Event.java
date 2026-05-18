package com.studyapp.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document(collection = "events")
public class Event {

    @Id
    private String id;

    private String userId;
    private String title;           // 일정 제목
    private String type;            // EXAM(시험) / ASSIGNMENT(과제) / OTHER
    private String subjectName;     // 관련 과목
    private LocalDate dueDate;      // 마감일 (D-Day 기준)
    private String description;     // 상세 설명
    private boolean completed;      // 완료 여부
    private LocalDateTime createdAt = LocalDateTime.now();
}
