package com.prepiq.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProblemResponse {
    private Long id;
    private String name;
    private String topic;
    private String difficulty;
    private String status;
    private String mistakeNote;
    private LocalDate solvedDate;
}