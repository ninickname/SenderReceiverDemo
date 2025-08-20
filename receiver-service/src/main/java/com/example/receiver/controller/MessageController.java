package com.example.receiver.controller;

import com.example.receiver.entity.Message;
import com.example.receiver.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/receiver")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "receiver-service",
            "java_version", System.getProperty("java.version")
        ));
    }

    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getAllMessages() {
        List<Message> messages = messageService.getAllMessages();
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/messages/topic/{topic}")
    public ResponseEntity<List<Message>> getMessagesByTopic(@PathVariable String topic) {
        List<Message> messages = messageService.getMessagesByTopic(topic);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/messages/count")
    public ResponseEntity<Map<String, Long>> getMessageCount() {
        long count = messageService.getMessageCount();
        return ResponseEntity.ok(Map.of("total_messages", count));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        long totalMessages = messageService.getMessageCount();
        return ResponseEntity.ok(Map.of(
            "total_messages", totalMessages,
            "service_status", "running",
            "scheduled_tasks", "active"
        ));
    }
}