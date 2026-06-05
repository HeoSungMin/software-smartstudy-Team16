package com.example.studyapp2.dto;

public class SubjectStat {
    private String subject;
    private int accuracy;
    private String level;
    private String color;

    public SubjectStat(String subject, int accuracy, String level, String color) {
        this.subject = subject;
        this.accuracy = accuracy;
        this.level = level;
        this.color = color;
    }

    public String getSubject() { return subject; }
    public int getAccuracy() { return accuracy; }
    public String getLevel() { return level; }
    public String getColor() { return color; }

    public String getLevelLabel() {
        return switch (level) {
            case "weak" -> "취약";
            case "mid"  -> "보통";
            default     -> "우수";
        };
    }

    public String getLevelTag() {
        return switch (level) {
            case "weak" -> "tag-weak";
            case "mid"  -> "tag-mid";
            default     -> "tag-ok";
        };
    }
}
