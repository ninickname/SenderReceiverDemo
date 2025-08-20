package com.example.receiver.controller;

import com.example.receiver.entity.ReceiverHeartbeat;
import com.example.receiver.entity.SenderHeartbeat;
import com.example.receiver.repository.ReceiverHeartbeatRepository;
import com.example.receiver.repository.SenderHeartbeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/heartbeat")
public class HeartbeatController {

    @Autowired
    private SenderHeartbeatRepository senderHeartbeatRepository;

    @Autowired
    private ReceiverHeartbeatRepository receiverHeartbeatRepository;

    @GetMapping("/sender")
    public List<SenderHeartbeat> getSenderHeartbeats(@RequestParam(defaultValue = "10") int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("receivedAt").descending());
        return senderHeartbeatRepository.findAll(pageable).getContent();
    }

    @GetMapping("/receiver")
    public List<ReceiverHeartbeat> getReceiverHeartbeats(@RequestParam(defaultValue = "10") int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("createdAt").descending());
        return receiverHeartbeatRepository.findAll(pageable).getContent();
    }

    @GetMapping("/stats")
    public Map<String, Object> getHeartbeatStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Get recent heartbeats (last minute)
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        LocalDateTime now = LocalDateTime.now();
        
        List<SenderHeartbeat> recentSenderHeartbeats = senderHeartbeatRepository.findByReceivedAtBetween(oneMinuteAgo, now);
        List<ReceiverHeartbeat> recentReceiverHeartbeats = receiverHeartbeatRepository.findByCreatedAtBetween(oneMinuteAgo, now);
        
        stats.put("sender_heartbeats_last_minute", recentSenderHeartbeats.size());
        stats.put("receiver_heartbeats_last_minute", recentReceiverHeartbeats.size());
        stats.put("total_sender_heartbeats", senderHeartbeatRepository.count());
        stats.put("total_receiver_heartbeats", receiverHeartbeatRepository.count());
        
        // Get latest heartbeats
        if (!recentSenderHeartbeats.isEmpty()) {
            stats.put("latest_sender_heartbeat", recentSenderHeartbeats.get(0));
        }
        if (!recentReceiverHeartbeats.isEmpty()) {
            stats.put("latest_receiver_heartbeat", recentReceiverHeartbeats.get(0));
        }
        
        return stats;
    }
}