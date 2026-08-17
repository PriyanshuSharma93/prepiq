package com.prepiq.backend.repository;

import com.prepiq.backend.model.MockInterviewSession;
import com.prepiq.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MockInterviewSessionRepository extends JpaRepository<MockInterviewSession, Long> {
    List<MockInterviewSession> findByUserOrderByStartedAtDesc(User user);
}