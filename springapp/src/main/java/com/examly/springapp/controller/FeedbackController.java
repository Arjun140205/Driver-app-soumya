package com.examly.springapp.controller;

import com.examly.springapp.dto.FeedbackDTO;
import com.examly.springapp.service.FeedbackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping("/feedback")
    public ResponseEntity<FeedbackDTO> createFeedback(@Valid @RequestBody FeedbackDTO feedback) {
        FeedbackDTO created = feedbackService.createFeedback(feedback);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/feedback/{feedbackId}")
    public ResponseEntity<FeedbackDTO> viewFeedbackById(@PathVariable Long feedbackId) {
        FeedbackDTO feedback = feedbackService.getFeedbackById(feedbackId);
        if (feedback != null) {
            return ResponseEntity.status(HttpStatus.OK).body(feedback);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/feedback")
    public ResponseEntity<List<FeedbackDTO>> viewAllFeedbacks() {
        List<FeedbackDTO> feedbacks = feedbackService.getAllFeedbacks();
        if (feedbacks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(feedbacks);
    }

    @GetMapping("/feedback/user/{userId}")
    public ResponseEntity<List<FeedbackDTO>> viewFeedbacksByUser(@PathVariable Long userId) {
        List<FeedbackDTO> feedbacks = feedbackService.getFeedbacksByUserId(userId);
        if (feedbacks == null || feedbacks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(feedbacks);
    }

    @DeleteMapping("/feedback/{feedbackId}")
    public ResponseEntity<FeedbackDTO> deleteFeedback(@PathVariable Long feedbackId) {
        FeedbackDTO deleted = feedbackService.deleteFeedback(feedbackId);
        if (deleted != null) {
            return ResponseEntity.status(HttpStatus.OK).body(deleted);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
