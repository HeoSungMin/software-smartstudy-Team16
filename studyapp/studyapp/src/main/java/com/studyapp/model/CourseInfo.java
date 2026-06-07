package com.studyapp.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Gemini API로부터 파싱된 강의 정보 DTO
 * DB에 저장하지 않고 검색 결과 전달용으로만 사용
 */
@Data
@NoArgsConstructor
public class CourseInfo {

    private String id;              // 임시 식별자 (프론트 체크박스용)
    private String universityName;  // 대학교명
    private String department;      // 학과명
    private String subjectName;     // 과목명
    private String professor;       // 담당 교수
    private String room;            // 강의실
    private int credit;             // 학점
    private String category;        // 전공필수 / 전공선택 / 교양

    // 시간 정보 (여러 시간대 가능 - 화목 등)
    private int dayOfWeek;          // 1=월 ~ 5=금
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;

    private String color;           // 자동 배정 색상 (hex)
}
