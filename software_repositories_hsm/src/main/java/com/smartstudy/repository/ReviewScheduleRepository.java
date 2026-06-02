package com.smartstudy.repository;

import com.smartstudy.domain.ReviewSchedule;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReviewScheduleRepository extends MongoRepository<ReviewSchedule, String> {
    List<ReviewSchedule> findByUserId(String userId);
    List<ReviewSchedule> findByNoteId(String noteId);
}
