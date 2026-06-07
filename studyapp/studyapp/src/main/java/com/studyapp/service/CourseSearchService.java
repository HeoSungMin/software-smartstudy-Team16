package com.studyapp.service;

import com.studyapp.model.CourseInfo;
import java.util.List;

/**
 * 강의 검색 서비스 인터페이스
 * 구현체를 교체하면 Gemini ↔ Claude 전환 가능
 */
public interface CourseSearchService {

    /**
     * 대학교명 + 학과명으로 수강 가능한 강의 목록 조회
     *
     * @param university 대학교명 (예: "경상국립대학교")
     * @param department 학과명  (예: "컴퓨터공학과")
     * @return 강의 목록
     */
    List<CourseInfo> searchCourses(String university, String department);
}
