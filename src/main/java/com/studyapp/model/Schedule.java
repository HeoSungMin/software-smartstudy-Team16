package com.studyapp.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "schedules")
public class Schedule {

    @Id
    private String id;

    private String userId;
    private String subjectName;   // 과목명
    private String color;         // 블록 색상 (hex)
    private int dayOfWeek;        // 1=월 ~ 5=금
    private int startHour;        // 시작 시간 (0~23)
    private int startMinute;      // 시작 분
    private int endHour;          // 종료 시간
    private int endMinute;        // 종료 분
    private String memo;          // 과목 메모 (F-4)
    private String professor;     // 교수명
    private String room;          // 강의실
}
