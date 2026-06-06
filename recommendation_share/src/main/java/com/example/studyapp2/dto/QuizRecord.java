package com.example.studyapp2.dto;

public class QuizRecord {
    private String subject;
    private int correct;
    private int total;
    private String daysAgo;

    public QuizRecord(String subject, int correct, int total, String daysAgo) {
        this.subject = subject;
        this.correct = correct;
        this.total = total;
        this.daysAgo = daysAgo;
    }

    public String getSubject() { return subject; }
    public int getCorrect() { return correct; }
    public int getTotal() { return total; }
    public String getDaysAgo() { return daysAgo; }

    public String getScoreColor() {
        double pct = total > 0 ? (double) correct / total * 100 : 0;
        if (pct < 60) return "#ff6b6b";
        if (pct < 80) return "#f5a623";
        return "#2bc5b4";
    }
}
