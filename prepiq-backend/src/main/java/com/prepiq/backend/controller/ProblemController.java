package com.prepiq.backend.controller;

import com.prepiq.backend.dto.ProblemRequest;
import com.prepiq.backend.dto.ProblemResponse;
import com.prepiq.backend.service.ProblemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/problems")
public class ProblemController {

    private final ProblemService problemService;

    public ProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    private Long currentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @PostMapping
    public ResponseEntity<ProblemResponse> create(@Valid @RequestBody ProblemRequest request) {
        ProblemResponse response = problemService.create(currentUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProblemResponse>> getAll() {
        return ResponseEntity.ok(problemService.getAllForUser(currentUserId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProblemResponse> update(@PathVariable Long id, @Valid @RequestBody ProblemRequest request) {
        ProblemResponse response = problemService.update(currentUserId(), id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        problemService.delete(currentUserId(), id);
        return ResponseEntity.noContent().build();
    }
}