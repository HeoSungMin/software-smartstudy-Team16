package com.example.studyapp2.dto;

public class RecommendationItem {
    private String subject;
    private String topic;
    private int accuracy;
    private String reason;
    private String priority;
    private String status;

    public RecommendationItem(String subject, String topic, int accuracy,
                               String reason, String priority, String status) {
        this.subject = subject;
        this.topic = topic;
        this.accuracy = accuracy;
        this.reason = reason;
        this.priority = priority;
        this.status = status;
    }

    public String getSubject() { return subject; }
    public String getTopic() { return topic; }
    public int getAccuracy() { return accuracy; }
    public String getReason() { return reason; }
    public String getPriority() { return priority; }
    public String getStatus() { return status; }

    public String getPriorityIcon() {
        return switch (priority) {
            case "high" -> "ti-alert-circle";
            case "mid"  -> "ti-refresh";
            default     -> "ti-check";
        };
    }

    public String getPriorityClass() {
        return switch (priority) {
            case "high" -> "red";
            case "mid"  -> "amber";
            default     -> "teal";
        };
    }
}
