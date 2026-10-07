package com.examly.springapp.service;

import com.examly.springapp.dto.FeedbackDTO;
import java.util.List;

public interface FeedbackService {
    FeedbackDTO createFeedback(FeedbackDTO feedback);
    FeedbackDTO getFeedbackById(Long feedbackId);
    List<FeedbackDTO> getAllFeedbacks();
    FeedbackDTO deleteFeedback(Long feedbackId);
    List<FeedbackDTO> getFeedbacksByUserId(Long userId);
}
