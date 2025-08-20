package com.example.receiver.service;

import com.example.receiver.entity.Message;
import com.example.receiver.repository.MessageRepository;
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
import java.util.List;

@Service
public class MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    @Autowired
    private MessageRepository messageRepository;

    @KafkaListener(topics = "sender-messages", groupId = "receiver-group")
    public void listen(@Payload String messageContent,
                      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                      @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                      @Header(KafkaHeaders.OFFSET) long offset) {
        
        logger.info("Received message from Kafka: topic={}, partition={}, offset={}, content='{}'", 
                   topic, partition, offset, messageContent);
        
        Message message = new Message(messageContent, topic, partition, offset);
        Message savedMessage = messageRepository.save(message);
        
        logger.info("Saved message to database with ID: {}", savedMessage.getId());
    }

    @Scheduled(fixedRate = 10000) // Every 10 seconds
    public void logMessageStats() {
        long totalMessages = messageRepository.count();
        long recentMessages = messageRepository.countMessagesSince(LocalDateTime.now().minusMinutes(5));
        
        logger.info("Message statistics - Total: {}, Last 5 minutes: {}", totalMessages, recentMessages);
    }

    @Scheduled(fixedRate = 10000) // Every 10 seconds
    public void processRecentMessages() {
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        List<Message> recentMessages = messageRepository.findByReceivedAtBetween(oneMinuteAgo, LocalDateTime.now());
        
        logger.info("Processing {} messages from the last minute", recentMessages.size());
        
        // Additional processing logic can be added here
        for (Message message : recentMessages) {
            logger.debug("Processing message ID: {} with content: '{}'", message.getId(), message.getContent());
        }
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAllOrderByReceivedAtDesc();
    }

    public List<Message> getMessagesByTopic(String topic) {
        return messageRepository.findBySourceTopic(topic);
    }

    public long getMessageCount() {
        return messageRepository.count();
    }
}