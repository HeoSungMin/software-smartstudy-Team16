package com.studyapp.controller;

import com.studyapp.model.CourseInfo;
import com.studyapp.service.CourseSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/courses") // 공통 경로
@RequiredArgsConstructor
public class CourseSearchController {

    private final CourseSearchService courseSearchService;

    // 수정 완료: 경로를 "/search"로 변경
    // 최종 URL: POST /api/courses/search
    @PostMapping("/search") 
    public ResponseEntity<?> searchCourses(@RequestBody Map<String, String> request) {
        try {
            log.info("강의 검색 요청 수신: {}, {}", request.get("university"), request.get("department"));
            
            String university = request.get("university");
            String department = request.get("department");
            
            List<CourseInfo> courses = courseSearchService.searchCourses(university, department);
            
            return ResponseEntity.ok(courses);
        } catch (Exception e) {
            log.error("검색 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("error", e.getMessage()));
        }
    }
}