package com.smartstudy.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "reviewSchedules")
public class ReviewSchedule {

    @Id
    private String id;
    private String userId;
    private String noteId;
    private LocalDateTime review1Day;
    private LocalDateTime review7Days;
    private LocalDateTime review30Days;
    private String status; // "pending" or "completed"
    private LocalDateTime createdAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getNoteId() { return noteId; }
    public void setNoteId(String noteId) { this.noteId = noteId; }

    public LocalDateTime getReview1Day() { return review1Day; }
    public void setReview1Day(LocalDateTime review1Day) { this.review1Day = review1Day; }

    public LocalDateTime getReview7Days() { return review7Days; }
    public void setReview7Days(LocalDateTime review7Days) { this.review7Days = review7Days; }

    public LocalDateTime getReview30Days() { return review30Days; }
    public void setReview30Days(LocalDateTime review30Days) { this.review30Days = review30Days; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
