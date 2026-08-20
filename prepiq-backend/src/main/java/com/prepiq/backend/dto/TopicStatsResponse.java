package com.prepiq.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicStatsResponse {
    private String topic;
    private int totalAttempts;
    private int solvedCount;
    private double solveRate;
    private String classification;
}