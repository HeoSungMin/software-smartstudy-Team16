package com.studyapp.controller;

import com.studyapp.model.CourseInfo;
import com.studyapp.service.CourseSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseSearchController {

    private final CourseSearchService courseSearchService;

    /**
     * POST /api/courses/search
     * Body: { "university": "경상국립대학교", "department": "컴퓨터공학과" }
     * Response: List<CourseInfo>
     */
    @PostMapping("/search")
    public ResponseEntity<?> searchCourses(@RequestBody Map<String, String> body) {
        String university = body.getOrDefault("university", "").trim();
        String department = body.getOrDefault("department", "").trim();

        if (university.isEmpty() || department.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "대학교명과 학과명을 모두 입력해주세요."));
        }

        try {
            List<CourseInfo> courses = courseSearchService.searchCourses(university, department);
            return ResponseEntity.ok(courses);
        } catch (RuntimeException e) {
            log.error("강의 검색 오류: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
