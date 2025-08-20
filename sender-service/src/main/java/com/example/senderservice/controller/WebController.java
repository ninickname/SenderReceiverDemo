package com.example.senderservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebController {
    
    private static final Logger logger = LoggerFactory.getLogger(WebController.class);
    
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
        
        model.addAttribute("successMessage", "Message logged successfully: " + message);
        return "index";
    }
}