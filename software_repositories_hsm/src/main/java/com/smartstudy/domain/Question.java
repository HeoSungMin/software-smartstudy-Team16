package com.smartstudy.domain;

public class Question {
    private String type; // "ox" or "short_answer"
    private String question;
    private String answer;

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
}
