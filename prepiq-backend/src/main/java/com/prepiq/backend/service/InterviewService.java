package com.prepiq.backend.service;

import com.prepiq.backend.dto.*;
import com.prepiq.backend.model.MockInterviewQuestion;
import com.prepiq.backend.model.MockInterviewSession;
import com.prepiq.backend.model.User;
import com.prepiq.backend.repository.MockInterviewQuestionRepository;
import com.prepiq.backend.repository.MockInterviewSessionRepository;
import com.prepiq.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InterviewService {

    private static final int MAX_QUESTIONS = 4;

    private final MockInterviewSessionRepository sessionRepository;
    private final MockInterviewQuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final DashboardService dashboardService;
    private final GeminiClient geminiClient;

    public InterviewService(MockInterviewSessionRepository sessionRepository,
                             MockInterviewQuestionRepository questionRepository,
                             UserRepository userRepository,
                             DashboardService dashboardService,
                             GeminiClient geminiClient) {
        this.sessionRepository = sessionRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.dashboardService = dashboardService;
        this.geminiClient = geminiClient;
    }

    public InterviewStartResponse startSession(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        List<TopicStatsResponse> weakTopics = dashboardService.getWeakTopics(userId).stream()
                .filter(t -> "Weak".equals(t.getClassification()))
                .toList();

        String targetTopic = weakTopics.isEmpty()
                ? "general Data Structures and Algorithms"
                : weakTopics.get(0).getTopic();

        String prompt = String.format(
                "You are a technical interviewer for a software engineering interview. " +
                "The candidate is weak in: %s. " +
                "Ask ONE concise technical DSA question focused on this topic. " +
                "Return ONLY the question text, no preamble, no markdown formatting.",
                targetTopic
        );

        String questionText = geminiClient.generateContent(prompt).trim();

        MockInterviewSession session = new MockInterviewSession();
        session.setUser(user);
        MockInterviewSession savedSession = sessionRepository.save(session);

        MockInterviewQuestion question = new MockInterviewQuestion();
        question.setSession(savedSession);
        question.setTopic(targetTopic);
        question.setQuestionText(questionText);
        question.setOrderIndex(1);
        MockInterviewQuestion savedQuestion = questionRepository.save(question);

        QuestionDTO dto = new QuestionDTO(1, targetTopic, questionText);
        return new InterviewStartResponse(savedSession.getId(), dto);
    }

    public AnswerResponse submitAnswer(Long userId, Long sessionId, String answer) {
        MockInterviewSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (!session.getUser().getId().equals(userId)) {
            throw new SecurityException("You do not have permission to access this session");
        }
        if (session.getEndedAt() != null) {
            throw new IllegalStateException("Session already ended");
        }

        List<MockInterviewQuestion> questions = questionRepository.findBySessionOrderByOrderIndexAsc(session);
        MockInterviewQuestion currentQuestion = questions.get(questions.size() - 1);
        currentQuestion.setUserAnswer(answer);

        String evalPrompt = String.format(
                "Question: %s\nCandidate's answer: %s\n" +
                "Evaluate this answer briefly in 2-3 sentences. Be direct and constructive. " +
                "Return ONLY the evaluation text, no preamble, no markdown formatting.",
                currentQuestion.getQuestionText(), answer
        );
        String evaluation = geminiClient.generateContent(evalPrompt).trim();
        currentQuestion.setAiEvaluation(evaluation);
        questionRepository.save(currentQuestion);

        boolean sessionComplete = questions.size() >= MAX_QUESTIONS;

        if (sessionComplete) {
            return new AnswerResponse(evaluation, true, null);
        }

        List<TopicStatsResponse> weakTopics = dashboardService.getWeakTopics(userId).stream()
                .filter(t -> "Weak".equals(t.getClassification()))
                .toList();
        String targetTopic = weakTopics.isEmpty()
                ? "general Data Structures and Algorithms"
                : weakTopics.get(questions.size() % weakTopics.size()).getTopic();

        String nextPrompt = String.format(
                "You are a technical interviewer. The candidate is weak in: %s. " +
                "Ask ONE concise technical DSA question focused on this topic, different from generic warmup questions. " +
                "Return ONLY the question text, no preamble, no markdown formatting.",
                targetTopic
        );
        String nextQuestionText = geminiClient.generateContent(nextPrompt).trim();

        MockInterviewQuestion nextQuestion = new MockInterviewQuestion();
        nextQuestion.setSession(session);
        nextQuestion.setTopic(targetTopic);
        nextQuestion.setQuestionText(nextQuestionText);
        nextQuestion.setOrderIndex(questions.size() + 1);
        questionRepository.save(nextQuestion);

        QuestionDTO nextDto = new QuestionDTO(questions.size() + 1, targetTopic, nextQuestionText);
        return new AnswerResponse(evaluation, false, nextDto);
    }

    public SessionEndResponse endSession(Long userId, Long sessionId) {
        MockInterviewSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (!session.getUser().getId().equals(userId)) {
            throw new SecurityException("You do not have permission to access this session");
        }

        if (session.getEndedAt() != null) {
            return new SessionEndResponse(session.getScore(), session.getFeedbackSummary());
        }

        List<MockInterviewQuestion> questions = questionRepository.findBySessionOrderByOrderIndexAsc(session);

        StringBuilder transcript = new StringBuilder();
        for (MockInterviewQuestion q : questions) {
            transcript.append("Q: ").append(q.getQuestionText()).append("\n");
            transcript.append("A: ").append(q.getUserAnswer() != null ? q.getUserAnswer() : "(no answer)").append("\n");
            transcript.append("Evaluation: ").append(q.getAiEvaluation() != null ? q.getAiEvaluation() : "").append("\n\n");
        }

        String summaryPrompt = String.format(
                "Here is a technical interview transcript:\n%s\n" +
                "Based on this transcript, provide:\n" +
                "1. A score from 0-100 representing overall performance\n" +
                "2. A 2-3 sentence feedback summary with concrete improvement suggestions\n" +
                "Return ONLY valid JSON in this exact format, no markdown, no code fences: " +
                "{\"score\": 75, \"feedback\": \"your feedback text here\"}",
                transcript.toString()
        );

        String result = geminiClient.generateContent(summaryPrompt).trim();
        result = result.replace("```json", "").replace("```", "").trim();

        int score;
        String feedback;
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(result);
            score = node.path("score").asInt(60);
            feedback = node.path("feedback").asText("Good effort. Keep practicing your weak topics.");
        } catch (Exception e) {
            score = 60;
            feedback = "Session completed. Keep practicing your weak topics for improvement.";
        }

        session.setEndedAt(java.time.LocalDateTime.now());
        session.setScore(score);
        session.setFeedbackSummary(feedback);
        sessionRepository.save(session);

        return new SessionEndResponse(score, feedback);
    }

    public List<SessionHistoryResponse> getHistory(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        List<MockInterviewSession> sessions = sessionRepository.findByUserOrderByStartedAtDesc(user);

        return sessions.stream()
                .filter(s -> s.getEndedAt() != null)
                .map(s -> {
                    List<String> topics = questionRepository.findBySessionOrderByOrderIndexAsc(s).stream()
                            .map(MockInterviewQuestion::getTopic)
                            .distinct()
                            .collect(Collectors.toList());
                    return new SessionHistoryResponse(s.getId(), s.getStartedAt(), s.getEndedAt(), s.getScore(), topics);
                })
                .toList();
    }
}