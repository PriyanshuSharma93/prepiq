package com.prepiq.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionHistoryResponse {
    private Long sessionId;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private Integer score;
    private List<String> topicsCovered;
}