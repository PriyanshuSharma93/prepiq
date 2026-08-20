package com.prepiq.backend.service;

import com.prepiq.backend.dto.TopicStatsResponse;
import com.prepiq.backend.model.Problem;
import com.prepiq.backend.model.User;
import com.prepiq.backend.repository.ProblemRepository;
import com.prepiq.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final ProblemRepository problemRepository;
    private final UserRepository userRepository;

    public DashboardService(ProblemRepository problemRepository, UserRepository userRepository) {
        this.problemRepository = problemRepository;
        this.userRepository = userRepository;
    }

    public List<TopicStatsResponse> getWeakTopics(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        List<Problem> problems = problemRepository.findByUser(user);

        Map<String, List<Problem>> byTopic = problems.stream()
                .collect(Collectors.groupingBy(Problem::getTopic));

        return byTopic.entrySet().stream()
                .map(entry -> {
                    String topic = entry.getKey();
                    List<Problem> topicProblems = entry.getValue();

                    int total = topicProblems.size();
                    long solved = topicProblems.stream()
                            .filter(p -> "Solved".equals(p.getStatus()))
                            .count();
                    long failedOrAttempted = topicProblems.stream()
                            .filter(p -> "Failed".equals(p.getStatus()) || "Attempted".equals(p.getStatus()))
                            .count();

                    double solveRate = total == 0 ? 0.0 : (double) solved / total;

                    String classification;
                    if (solveRate < 0.5 || failedOrAttempted >= 2) {
                        classification = "Weak";
                    } else if (solveRate >= 0.8 && total >= 2) {
                        classification = "Strong";
                    } else {
                        classification = "Developing";
                    }

                    return new TopicStatsResponse(topic, total, (int) solved,
                            Math.round(solveRate * 100.0) / 100.0, classification);
                })
                .sorted((a, b) -> Integer.compare(b.getTotalAttempts(), a.getTotalAttempts()))
                .toList();
    }
}