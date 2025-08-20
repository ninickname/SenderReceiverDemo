package com.example.senderservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@RestController
@RequestMapping("/api/sender")
public class SenderController {

    private static final Logger logger = LoggerFactory.getLogger(SenderController.class);
    private static final String TOPIC = "sender-messages";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "sender-service",
            "java_version", System.getProperty("java.version")
        ));
    }

    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendMessage(@RequestBody String message) {
        try {
            kafkaTemplate.send(TOPIC, message);
            logger.info("Message sent to Kafka topic '{}': {}", TOPIC, message);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Message sent to Kafka",
                "content", message,
                "topic", TOPIC
            ));
        } catch (Exception e) {
            logger.error("Failed to send message to Kafka: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "error",
                "message", "Failed to send message to Kafka",
                "error", e.getMessage()
            ));
        }
    }
}