package com.studyapp.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document(collection = "events")
public class Event {
    @Id
    private String id;
    private String userId;
    private String title;
    private String type;
    private String subjectName;
    private LocalDate dueDate;
    private String description;
    private boolean completed;
    private LocalDateTime createdAt = LocalDateTime.now();
}
