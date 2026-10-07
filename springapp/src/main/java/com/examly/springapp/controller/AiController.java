package com.examly.springapp.controller;

import com.examly.springapp.dto.DriverDTO;
import com.examly.springapp.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private AiService aiService;

    @PostMapping("/driver-search")
    public ResponseEntity<List<DriverDTO>> searchDrivers(@RequestBody String query) {
        return ResponseEntity.ok(aiService.searchDrivers(query));
    }

    @GetMapping("/driver/{driverId}/feedback-summary")
    public ResponseEntity<Map<String, Object>> getFeedbackSummary(@PathVariable Long driverId) {
        return ResponseEntity.ok(aiService.summarizeDriverFeedback(driverId));
    }

    @PostMapping("/feedback/analyze-existing")
    public ResponseEntity<Map<String, Integer>> analyzeExistingFeedback() {
        Map<String, Integer> response = new HashMap<>();
        response.put("updated", aiService.analyzeExistingFeedback());
        return ResponseEntity.ok(response);
    }
}
