package com.example.studyapp2.service;

import com.example.studyapp2.dto.QuizRecord;
import com.example.studyapp2.dto.RecommendationItem;
import com.example.studyapp2.dto.SubjectStat;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendationService {

    public List<SubjectStat> getSubjectStats() {
        return List.of(
            new SubjectStat("자료구조",       42, "weak", "#ff6b6b"),
            new SubjectStat("컴퓨터네트워크", 55, "mid",  "#f5a623"),
            new SubjectStat("운영체제",       61, "mid",  "#f5a623"),
            new SubjectStat("알고리즘",       88, "ok",   "#2bc5b4")
        );
    }

    public List<RecommendationItem> getRecommendations() {
        return List.of(
            new RecommendationItem("자료구조", "스택/큐 집중 복습", 42,
                "정답률 42% · 최근 오답률 높음 · 즉시 복습 권장", "high", "active"),
            new RecommendationItem("컴퓨터네트워크", "TCP/IP 노트 복습", 55,
                "정답률 55% · 에빙하우스 복습 주기 도래", "mid", "active"),
            new RecommendationItem("알고리즘", "유지 중, 다음 복습 예약됨", 88,
                "정답률 88% · 7일 후 복습 캘린더 등록 완료", "low", "scheduled")
        );
    }

    public List<QuizRecord> getRecentHistory() {
        return List.of(
            new QuizRecord("자료구조",       3, 5, "오늘"),
            new QuizRecord("운영체제",       4, 5, "어제"),
            new QuizRecord("알고리즘",       5, 5, "2일 전"),
            new QuizRecord("컴퓨터네트워크", 3, 5, "3일 전"),
            new QuizRecord("자료구조",       2, 5, "4일 전")
        );
    }

    public long getWeakCount() {
        return getSubjectStats().stream()
                .filter(s -> "weak".equals(s.getLevel())).count();
    }

    public int getAvgAccuracy() {
        return (int) getSubjectStats().stream()
                .mapToInt(SubjectStat::getAccuracy).average().orElse(0);
    }
}
