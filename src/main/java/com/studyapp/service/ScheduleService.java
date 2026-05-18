package com.studyapp.service;

import com.studyapp.model.Schedule;
import com.studyapp.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public List<Schedule> getSchedulesByUser(String userId) {
        return scheduleRepository.findByUserId(userId);
    }

    public Schedule save(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    public Schedule findById(String id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("시간표를 찾을 수 없습니다."));
    }

    public Schedule updateMemo(String id, String userId, String memo) {
        Schedule schedule = findById(id);
        if (!schedule.getUserId().equals(userId)) {
            throw new SecurityException("권한이 없습니다.");
        }
        schedule.setMemo(memo);
        return scheduleRepository.save(schedule);
    }

    public void delete(String id, String userId) {
        scheduleRepository.deleteByIdAndUserId(id, userId);
    }
}
