package com.example.senderservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class HeartbeatService {

    private static final Logger logger = LoggerFactory.getLogger(HeartbeatService.class);
    private static final String KEEPALIVE_TOPIC = "keepalive";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedRate = 1000) // Every 1 second
    public void sendHeartbeat() {
        String timestamp = LocalDateTime.now().format(formatter);
        String heartbeatMessage = String.format("{\"service\":\"sender\",\"timestamp\":\"%s\",\"status\":\"alive\"}", timestamp);
        
        try {
            kafkaTemplate.send(KEEPALIVE_TOPIC, heartbeatMessage);
            logger.debug("Sender heartbeat sent: {}", heartbeatMessage);
        } catch (Exception e) {
            logger.error("Failed to send sender heartbeat", e);
        }
    }
}