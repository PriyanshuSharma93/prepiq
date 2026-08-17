package com.prepiq.backend.repository;

import com.prepiq.backend.model.MockInterviewQuestion;
import com.prepiq.backend.model.MockInterviewSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MockInterviewQuestionRepository extends JpaRepository<MockInterviewQuestion, Long> {
    List<MockInterviewQuestion> findBySessionOrderByOrderIndexAsc(MockInterviewSession session);
}