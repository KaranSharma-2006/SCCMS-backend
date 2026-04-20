package com.smartcity.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void complaintSubmittedEmail(String to, String userName, String complaintTitle) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Complaint Submitted Successfully");
            message.setText(
                    "Dear " + userName + ",\n\n" +
                    "Your complaint titled \"" + complaintTitle + "\" has been received successfully.\n\n" +
                    "Our team will review your complaint and take appropriate action. " +
                    "You will be notified once there is an update on your complaint.\n\n" +
                    "Your complaint is currently under review.\n\n" +
                    "Thank you for helping us improve our city!\n\n" +
                    "Best regards,\n" +
                    "Smart City Administration"
            );
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send complaint submission email: " + e.getMessage());
        }
    }

    @Async
    public void complaintStatusUpdatedEmail(String to, String userName, String complaintTitle,
                                             String newStatus, String adminReply) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Update on Your Complaint");

            StringBuilder body = new StringBuilder();
            body.append("Dear ").append(userName).append(",\n\n");
            body.append("The status of your complaint titled \"").append(complaintTitle)
                    .append("\" has been updated.\n\n");
            body.append("New Status: ").append(newStatus).append("\n\n");

            if (adminReply != null && !adminReply.isEmpty()) {
                body.append("Admin Reply:\n").append(adminReply).append("\n\n");
            }

            body.append("If you have any further questions, please don't hesitate to reach out.\n\n");
            body.append("Best regards,\n");
            body.append("Smart City Administration");

            message.setText(body.toString());
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send status update email: " + e.getMessage());
        }
    }
}
