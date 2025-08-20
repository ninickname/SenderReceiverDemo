package com.example.senderservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebController {
    
    private static final Logger logger = LoggerFactory.getLogger(WebController.class);
    private static final String TOPIC = "sender-messages";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    @GetMapping("/")
    public String index(Model model) {
        logger.info("Welcome page accessed");
        String javaVersion = System.getProperty("java.version");
        model.addAttribute("javaVersion", javaVersion);
        return "index";
    }
    
    @PostMapping("/submit")
    public String submitMessage(@RequestParam String message, Model model) {
        logger.info("Message received from web form: {}", message);
        
        try {
            kafkaTemplate.send(TOPIC, message);
            logger.info("Message sent to Kafka topic '{}': {}", TOPIC, message);
            model.addAttribute("successMessage", "Message sent to Kafka successfully: " + message);
        } catch (Exception e) {
            logger.error("Failed to send message to Kafka: {}", e.getMessage());
            model.addAttribute("errorMessage", "Failed to send message to Kafka: " + e.getMessage());
        }
        
        return "index";
    }
}