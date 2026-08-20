package com.prepiq.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerResponse {
    private String evaluation;
    private boolean sessionComplete;
    private QuestionDTO nextQuestion;
}