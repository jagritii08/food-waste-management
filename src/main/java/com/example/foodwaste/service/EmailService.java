
package com.example.foodwaste.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) {
        try {
            if (mailSender == null) {
                System.out.println("Email skipped (no mail config): " + subject);
                return;
            }
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            System.out.println("Email send failed: " + e.getMessage());
        }
    }

    public void sendFoodAvailableEmail(String ngoEmail, String foodName,
                                       String restaurantName, String quantity) {
        String subject = "🍱 Food Available — " + foodName;
        String body = "Hello!\n\n" +
                restaurantName + " has posted food:\n\n" +
                "Food: " + foodName + "\n" +
                "Quantity: " + quantity + "\n\n" +
                "Login to claim:\n" +
                "http://localhost:8081/login\n\n" +
                "FoodBridge Team";
        sendEmail(ngoEmail, subject, body);
    }

    public void sendRequestStatusEmail(String email, String orgName,
                                        String foodName, String status) {
        String subject = "📋 Request Update — " + foodName;
        String body = "Hello!\n\n" +
                orgName + " has updated the food request:\n\n" +
                "Food: " + foodName + "\n" +
                "Status: " + status + "\n\n" +
                "FoodBridge Team";
        sendEmail(email, subject, body);
    }
}