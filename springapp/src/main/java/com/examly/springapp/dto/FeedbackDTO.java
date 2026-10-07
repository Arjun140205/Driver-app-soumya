package com.examly.springapp.dto;

import java.time.LocalDate;
import com.examly.springapp.dto.validation.ValidationPatterns;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * Data Transfer Object for {@link com.examly.springapp.model.Feedback}.
 * Includes the AI generated attributes (sentiment, sentimentScore, aiTags), which the server
 * fills in when the feedback is created.
 */
public class FeedbackDTO {
    private Long feedbackId;
    @NotBlank(message = "Feedback text is required")
    private String feedbackText;

    private LocalDate date;

    @NotNull(message = "User is required")
    private UserDTO user;

    // optional, but when it is sent it must say which driver it is about
    private DriverDTO driver;

    @NotBlank(message = "Category is required")
    @Pattern(regexp = ValidationPatterns.FEEDBACK_CATEGORY,
            message = "Category must be Driver Performance, Service Experience, Punctuality, Vehicle Condition or Other")
    private String category;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Integer rating;
    private String sentiment;
    private Double sentimentScore;
    private String aiTags;

    public FeedbackDTO() {
    }

    public Long getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(Long feedbackId) {
        this.feedbackId = feedbackId;
    }

    public String getFeedbackText() {
        return feedbackText;
    }

    public void setFeedbackText(String feedbackText) {
        this.feedbackText = feedbackText;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public DriverDTO getDriver() {
        return driver;
    }

    public void setDriver(DriverDTO driver) {
        this.driver = driver;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public Double getSentimentScore() {
        return sentimentScore;
    }

    public void setSentimentScore(Double sentimentScore) {
        this.sentimentScore = sentimentScore;
    }

    public String getAiTags() {
        return aiTags;
    }

    public void setAiTags(String aiTags) {
        this.aiTags = aiTags;
    }

    // ---- checks of the nested ids (only used by validation, never part of the JSON) ----

    @JsonIgnore
    @AssertTrue(message = "user.userId is required")
    public boolean isUserIdProvided() {
        return user == null || user.getUserId() != null;
    }

    @JsonIgnore
    @AssertTrue(message = "driver.driverId is required")
    public boolean isDriverIdProvided() {
        return driver == null || driver.getDriverId() != null;
    }
}
