package com.studyapp.service;

import com.studyapp.model.CourseInfo;
import com.studyapp.model.Schedule;
import com.studyapp.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public List<Schedule> getByUser(String userId) {
        return scheduleRepository.findByUserId(userId);
    }

    public Schedule save(Schedule s) {
        return scheduleRepository.save(s);
    }

    public Schedule findById(String id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("시간표를 찾을 수 없습니다."));
    }

    public Schedule updateMemo(String id, String userId, String memo) {
        Schedule s = findById(id);
        if (!s.getUserId().equals(userId)) throw new SecurityException("권한이 없습니다.");
        s.setMemo(memo);
        return scheduleRepository.save(s);
    }

    public void delete(String id, String userId) {
        scheduleRepository.deleteByIdAndUserId(id, userId);
    }

    /** CourseInfo 목록을 Schedule로 일괄 변환 저장 */
    public List<Schedule> bulkRegister(String userId, List<CourseInfo> courses) {
        List<Schedule> schedules = courses.stream()
                .map(c -> {
                    Schedule s = new Schedule();
                    s.setUserId(userId);
                    s.setSubjectName(c.getSubjectName());
                    s.setProfessor(c.getProfessor());
                    s.setRoom(c.getRoom());
                    s.setDayOfWeek(c.getDayOfWeek());
                    s.setStartHour(c.getStartHour());
                    s.setStartMinute(c.getStartMinute());
                    s.setEndHour(c.getEndHour());
                    s.setEndMinute(c.getEndMinute());
                    s.setColor(c.getColor());
                    return s;
                })
                .toList();
        return scheduleRepository.saveAll(schedules);
    }
}
