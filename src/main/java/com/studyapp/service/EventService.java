package com.studyapp.service;

import com.studyapp.model.Event;
import com.studyapp.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public List<Event> getUpcomingEvents(String userId) {
        return eventRepository.findByUserIdAndDueDateGreaterThanEqualOrderByDueDateAsc(
                userId, LocalDate.now());
    }

    public List<Event> getAllEvents(String userId) {
        return eventRepository.findByUserIdOrderByDueDateAsc(userId);
    }

    public Event save(Event event) {
        return eventRepository.save(event);
    }

    public Event findById(String id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다."));
    }

    public void toggleComplete(String id, String userId) {
        Event event = findById(id);
        if (!event.getUserId().equals(userId)) {
            throw new SecurityException("권한이 없습니다.");
        }
        event.setCompleted(!event.isCompleted());
        eventRepository.save(event);
    }

    public void delete(String id) {
        eventRepository.deleteById(id);
    }

    // D-Day 계산: 오늘 기준 남은 일수 (음수면 지난 날)
    public long calcDday(LocalDate dueDate) {
        return ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
    }

    // 이벤트에 D-Day 값을 붙여서 Map 리스트로 반환
    public List<Map<String, Object>> getEventsWithDday(String userId) {
        return getUpcomingEvents(userId).stream()
                .map(e -> Map.of(
                        "event", e,
                        "dday", calcDday(e.getDueDate())
                ))
                .collect(Collectors.toList());
    }
}
