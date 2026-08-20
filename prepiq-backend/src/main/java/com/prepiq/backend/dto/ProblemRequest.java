package com.prepiq.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProblemRequest {

    @NotBlank(message = "Problem name is required")
    @Size(max = 200)
    private String name;

    @NotBlank(message = "Topic is required")
    private String topic;

    @NotBlank(message = "Difficulty is required")
    private String difficulty;

    @NotBlank(message = "Status is required")
    private String status;

    @Size(max = 1000)
    private String mistakeNote;

    @NotNull(message = "Solved date is required")
    @PastOrPresent(message = "Solved date cannot be in the future")
    private LocalDate solvedDate;
}