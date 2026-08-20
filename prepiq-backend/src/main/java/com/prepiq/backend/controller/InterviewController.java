package com.prepiq.backend.controller;

import com.prepiq.backend.dto.*;
import com.prepiq.backend.service.InterviewService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interview")
public class InterviewController {

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    private Long currentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @PostMapping("/start")
    public ResponseEntity<InterviewStartResponse> start() {
        return ResponseEntity.ok(interviewService.startSession(currentUserId()));
    }

    @PostMapping("/{sessionId}/answer")
    public ResponseEntity<AnswerResponse> answer(@PathVariable Long sessionId, @Valid @RequestBody AnswerRequest request) {
        return ResponseEntity.ok(interviewService.submitAnswer(currentUserId(), sessionId, request.getAnswer()));
    }

    @PostMapping("/{sessionId}/end")
    public ResponseEntity<SessionEndResponse> end(@PathVariable Long sessionId) {
        return ResponseEntity.ok(interviewService.endSession(currentUserId(), sessionId));
    }

    @GetMapping("/history")
    public ResponseEntity<List<SessionHistoryResponse>> history() {
        return ResponseEntity.ok(interviewService.getHistory(currentUserId()));
    }
}