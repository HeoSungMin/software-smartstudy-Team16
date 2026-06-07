package com.studyapp.service;

import com.studyapp.model.Event;
import com.studyapp.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    public List<Event> getUpcoming(String userId) {
        return eventRepository.findByUserIdAndDueDateGreaterThanEqualOrderByDueDateAsc(userId, LocalDate.now());
    }
    public List<Event> getAll(String userId) { return eventRepository.findByUserIdOrderByDueDateAsc(userId); }
    public Event save(Event e) { return eventRepository.save(e); }
    public Event findById(String id) {
        return eventRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다."));
    }
    public void toggleComplete(String id, String userId) {
        Event e = findById(id);
        if (!e.getUserId().equals(userId)) throw new SecurityException("권한이 없습니다.");
        e.setCompleted(!e.isCompleted());
        eventRepository.save(e);
    }
    public void delete(String id) { eventRepository.deleteById(id); }
    public long calcDday(LocalDate dueDate) { return ChronoUnit.DAYS.between(LocalDate.now(), dueDate); }

    public List<Map<String, Object>> getWithDday(String userId) {
        return getUpcoming(userId).stream()
                .map(e -> Map.of("event", e, "dday", calcDday(e.getDueDate())))
                .collect(Collectors.toList());
    }
}
