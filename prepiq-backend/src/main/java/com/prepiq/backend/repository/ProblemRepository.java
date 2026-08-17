package com.prepiq.backend.repository;

import com.prepiq.backend.model.Problem;
import com.prepiq.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProblemRepository extends JpaRepository<Problem, Long> {
    List<Problem> findByUser(User user);
    List<Problem> findByUserOrderBySolvedDateDesc(User user);
} 