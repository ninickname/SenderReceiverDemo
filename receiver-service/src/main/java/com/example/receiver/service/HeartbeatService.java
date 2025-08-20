package com.example.receiver.service;

import com.example.receiver.entity.ReceiverHeartbeat;
import com.example.receiver.entity.SenderHeartbeat;
import com.example.receiver.repository.ReceiverHeartbeatRepository;
import com.example.receiver.repository.SenderHeartbeatRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class HeartbeatService {

    private static final Logger logger = LoggerFactory.getLogger(HeartbeatService.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private ReceiverHeartbeatRepository receiverHeartbeatRepository;

    @Autowired
    private SenderHeartbeatRepository senderHeartbeatRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Scheduled(fixedRate = 1000) // Every 1 second
    public void saveReceiverHeartbeat() {
        try {
            ReceiverHeartbeat heartbeat = new ReceiverHeartbeat("receiver", "alive");
            receiverHeartbeatRepository.save(heartbeat);
            logger.debug("Receiver heartbeat saved with ID: {}", heartbeat.getId());
        } catch (Exception e) {
            logger.error("Failed to save receiver heartbeat", e);
        }
    }

    @KafkaListener(topics = "keepalive", groupId = "heartbeat-group")
    public void handleSenderHeartbeat(@Payload String heartbeatJson,
                                    @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                    @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                    @Header(KafkaHeaders.OFFSET) long offset) {
        try {
            JsonNode jsonNode = objectMapper.readTree(heartbeatJson);
            String serviceName = jsonNode.get("service").asText();
            String timestampStr = jsonNode.get("timestamp").asText();
            String status = jsonNode.get("status").asText();
            
            LocalDateTime timestamp = LocalDateTime.parse(timestampStr, formatter);
            
            SenderHeartbeat senderHeartbeat = new SenderHeartbeat(serviceName, timestamp, status, topic, partition, offset);
            senderHeartbeatRepository.save(senderHeartbeat);
            
            logger.debug("Sender heartbeat saved with ID: {} from service: {}", senderHeartbeat.getId(), serviceName);
        } catch (Exception e) {
            logger.error("Failed to process sender heartbeat: {}", heartbeatJson, e);
        }
    }
}