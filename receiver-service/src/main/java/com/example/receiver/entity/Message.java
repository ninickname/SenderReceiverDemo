package com.example.receiver.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String content;
    
    @Column(name = "received_at", nullable = false)
    private LocalDateTime receivedAt;
    
    @Column(name = "source_topic")
    private String sourceTopic;
    
    @Column(name = "partition_id")
    private Integer partitionId;
    
    @Column(name = "offset_value")
    private Long offsetValue;

    public Message() {}
    
    public Message(String content, String sourceTopic, Integer partitionId, Long offsetValue) {
        this.content = content;
        this.sourceTopic = sourceTopic;
        this.partitionId = partitionId;
        this.offsetValue = offsetValue;
        this.receivedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }

    public String getSourceTopic() {
        return sourceTopic;
    }

    public void setSourceTopic(String sourceTopic) {
        this.sourceTopic = sourceTopic;
    }

    public Integer getPartitionId() {
        return partitionId;
    }

    public void setPartitionId(Integer partitionId) {
        this.partitionId = partitionId;
    }

    public Long getOffsetValue() {
        return offsetValue;
    }

    public void setOffsetValue(Long offsetValue) {
        this.offsetValue = offsetValue;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", receivedAt=" + receivedAt +
                ", sourceTopic='" + sourceTopic + '\'' +
                ", partitionId=" + partitionId +
                ", offsetValue=" + offsetValue +
                '}';
    }
}