package com.smartstudy.repository;

import com.smartstudy.domain.Note;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NoteRepository extends MongoRepository<Note, String> {
    List<Note> findByUserId(String userId);
    List<Note> findAllByOrderByCreatedAtDesc();
}
