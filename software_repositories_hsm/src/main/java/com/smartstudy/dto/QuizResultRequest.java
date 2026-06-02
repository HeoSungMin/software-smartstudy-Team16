package com.smartstudy.dto;

import java.util.List;

public class QuizResultRequest {
    private List<Integer> wrongIndices;

    public List<Integer> getWrongIndices() { return wrongIndices; }
    public void setWrongIndices(List<Integer> wrongIndices) { this.wrongIndices = wrongIndices; }
}
