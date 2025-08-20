package com.example.receiver.repository;

import com.example.receiver.entity.SenderHeartbeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SenderHeartbeatRepository extends JpaRepository<SenderHeartbeat, Long> {
    
    @Query("SELECT sh FROM SenderHeartbeat sh WHERE sh.receivedAt BETWEEN :startTime AND :endTime ORDER BY sh.receivedAt DESC")
    List<SenderHeartbeat> findByReceivedAtBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT sh FROM SenderHeartbeat sh ORDER BY sh.receivedAt DESC")
    List<SenderHeartbeat> findAllOrderByReceivedAtDesc();
}