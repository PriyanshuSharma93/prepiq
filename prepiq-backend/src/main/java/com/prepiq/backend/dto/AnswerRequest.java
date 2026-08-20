package com.prepiq.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AnswerRequest {

    @NotBlank(message = "Answer is required")
    @Size(max = 3000)
    private String answer;
}