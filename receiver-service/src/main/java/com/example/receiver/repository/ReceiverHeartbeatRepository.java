package com.example.receiver.repository;

import com.example.receiver.entity.ReceiverHeartbeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReceiverHeartbeatRepository extends JpaRepository<ReceiverHeartbeat, Long> {
    
    @Query("SELECT rh FROM ReceiverHeartbeat rh WHERE rh.createdAt BETWEEN :startTime AND :endTime ORDER BY rh.createdAt DESC")
    List<ReceiverHeartbeat> findByCreatedAtBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT rh FROM ReceiverHeartbeat rh ORDER BY rh.createdAt DESC")
    List<ReceiverHeartbeat> findAllOrderByCreatedAtDesc();
}