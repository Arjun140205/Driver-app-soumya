package com.examly.springapp.service;

import com.examly.springapp.dto.FeedbackDTO;
import com.examly.springapp.mapper.DtoMapper;
import com.examly.springapp.model.Feedback;
import com.examly.springapp.repository.FeedbackRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepo feedbackRepo;
    private final AiService aiService;

    public FeedbackServiceImpl(FeedbackRepo feedbackRepo, AiService aiService) {
        this.feedbackRepo = feedbackRepo;
        this.aiService = aiService;
    }

    @Override
    public FeedbackDTO createFeedback(FeedbackDTO feedbackDto) {
        Feedback feedback = DtoMapper.toEntity(feedbackDto);
        if (feedback.getDate() == null) {
            feedback.setDate(LocalDate.now());
        }
        try {
            // AI sentiment analysis + auto-tagging; feedback is still saved if the analysis fails.
            aiService.applyAnalysis(feedback);
        } catch (Exception ignored) {
            // keep the feedback without AI attributes
        }
        return DtoMapper.toDTO(feedbackRepo.save(feedback));
    }

    @Override
    public FeedbackDTO getFeedbackById(Long feedbackId) {
        return feedbackRepo.findById(feedbackId).map(DtoMapper::toDTO).orElse(null);
    }

    @Override
    public List<FeedbackDTO> getAllFeedbacks() {
        return DtoMapper.toFeedbackDTOs(feedbackRepo.findAll());
    }

    @Override
    public FeedbackDTO deleteFeedback(Long feedbackId) {
        Optional<Feedback> feedbackOpt = feedbackRepo.findById(feedbackId);
        if (feedbackOpt.isPresent()) {
            feedbackRepo.delete(feedbackOpt.get());
            return DtoMapper.toDTO(feedbackOpt.get());
        }
        return null;
    }

    @Override
    public List<FeedbackDTO> getFeedbacksByUserId(Long userId) {
        return DtoMapper.toFeedbackDTOs(feedbackRepo.findByUserUserId(userId));
    }
}
