package com.prepiq.backend.service;

import com.prepiq.backend.dto.ProblemRequest;
import com.prepiq.backend.dto.ProblemResponse;
import com.prepiq.backend.model.Problem;
import com.prepiq.backend.model.User;
import com.prepiq.backend.repository.ProblemRepository;
import com.prepiq.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final UserRepository userRepository;

    public ProblemService(ProblemRepository problemRepository, UserRepository userRepository) {
        this.problemRepository = problemRepository;
        this.userRepository = userRepository;
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    private ProblemResponse toResponse(Problem p) {
        return new ProblemResponse(p.getId(), p.getName(), p.getTopic(), p.getDifficulty(),
                p.getStatus(), p.getMistakeNote(), p.getSolvedDate());
    }

    public ProblemResponse create(Long userId, ProblemRequest request) {
        User user = getUserOrThrow(userId);

        Problem problem = new Problem();
        problem.setUser(user);
        problem.setName(request.getName());
        problem.setTopic(request.getTopic());
        problem.setDifficulty(request.getDifficulty());
        problem.setStatus(request.getStatus());
        problem.setMistakeNote(request.getMistakeNote());
        problem.setSolvedDate(request.getSolvedDate());

        Problem saved = problemRepository.save(problem);
        return toResponse(saved);
    }

    public List<ProblemResponse> getAllForUser(Long userId) {
        User user = getUserOrThrow(userId);
        return problemRepository.findByUserOrderBySolvedDateDesc(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ProblemResponse update(Long userId, Long problemId, ProblemRequest request) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new IllegalArgumentException("Problem not found"));

        if (!problem.getUser().getId().equals(userId)) {
            throw new SecurityException("You do not have permission to modify this problem");
        }

        problem.setName(request.getName());
        problem.setTopic(request.getTopic());
        problem.setDifficulty(request.getDifficulty());
        problem.setStatus(request.getStatus());
        problem.setMistakeNote(request.getMistakeNote());
        problem.setSolvedDate(request.getSolvedDate());

        Problem updated = problemRepository.save(problem);
        return toResponse(updated);
    }

    public void delete(Long userId, Long problemId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new IllegalArgumentException("Problem not found"));

        if (!problem.getUser().getId().equals(userId)) {
            throw new SecurityException("You do not have permission to delete this problem");
        }

        problemRepository.delete(problem);
    }
}