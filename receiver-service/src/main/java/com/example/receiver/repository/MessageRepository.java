package com.example.receiver.repository;

import com.example.receiver.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    List<Message> findBySourceTopic(String sourceTopic);
    
    List<Message> findByReceivedAtBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.receivedAt >= :since")
    long countMessagesSince(LocalDateTime since);
    
    @Query("SELECT m FROM Message m ORDER BY m.receivedAt DESC")
    List<Message> findAllOrderByReceivedAtDesc();
}