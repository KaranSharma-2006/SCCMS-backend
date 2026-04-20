package com.smartcity.service;

import com.smartcity.dto.request.ComplaintRequest;
import com.smartcity.dto.request.ComplaintStatusUpdateRequest;
import com.smartcity.dto.response.ComplaintResponse;
import com.smartcity.entity.Complaint;
import com.smartcity.entity.User;
import com.smartcity.enums.Category;
import com.smartcity.enums.ComplaintStatus;
import com.smartcity.exception.BadRequestException;
import com.smartcity.exception.ResourceNotFoundException;
import com.smartcity.repository.ComplaintRepository;
import com.smartcity.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComplaintService {

    private static final Logger logger = LoggerFactory.getLogger(ComplaintService.class);

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public ComplaintService(ComplaintRepository complaintRepository,
                            UserRepository userRepository,
                            EmailService emailService) {
        this.complaintRepository = complaintRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public ComplaintResponse createComplaint(ComplaintRequest request) {
        User user = getCurrentUser();

        Category category;
        try {
            category = Category.valueOf(request.getCategory().toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid complaint category submitted: {}", request.getCategory());
            throw new BadRequestException("Invalid category: " + request.getCategory() +
                    ". Valid values: ROAD, WATER, ELECTRICITY, SANITATION, OTHER");
        }

        Complaint complaint = new Complaint();
        complaint.setTitle(request.getTitle());
        complaint.setDescription(request.getDescription());
        complaint.setCategory(category);
        complaint.setStatus(ComplaintStatus.PENDING);
        complaint.setUser(user);

        Complaint saved = complaintRepository.save(complaint);
        logger.info("New complaint created with ID: {} by user: {}", saved.getId(), user.getEmail());

        // Send email notification
        emailService.complaintSubmittedEmail(user.getEmail(), user.getName(), saved.getTitle());

        return mapToResponse(saved);
    }

    public List<ComplaintResponse> getMyComplaints() {
        User user = getCurrentUser();
        List<Complaint> complaints = complaintRepository.findByUserOrderByCreatedAtDesc(user);
        return complaints.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<ComplaintResponse> getAllComplaints() {
        List<Complaint> complaints = complaintRepository.findAllByOrderByCreatedAtDesc();
        return complaints.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public ComplaintResponse getComplaintById(Long id) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Complaint retrieval failed: No complaint found with ID {}", id);
                    return new ResourceNotFoundException("Complaint not found with id: " + id);
                });

        // Users can only view their own complaints
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (currentUser.getRole().name().equals("USER") &&
                !complaint.getUser().getId().equals(currentUser.getId())) {
            logger.warn("Access denied: User {} tried to view complaint {} belonging to user {}",
                    currentUser.getEmail(), id, complaint.getUser().getEmail());
            throw new BadRequestException("You can only view your own complaints");
        }

        return mapToResponse(complaint);
    }

    public ComplaintResponse updateComplaintStatus(Long id, ComplaintStatusUpdateRequest request) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + id));

        ComplaintStatus newStatus;
        try {
            newStatus = ComplaintStatus.valueOf(request.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid complaint status update: {} for ID {}", request.getStatus(), id);
            throw new BadRequestException("Invalid status: " + request.getStatus() +
                    ". Valid values: PENDING, IN_PROGRESS, RESOLVED, REJECTED");
        }

        complaint.setStatus(newStatus);
        if (request.getAdminReply() != null) {
            complaint.setAdminReply(request.getAdminReply());
        }

        Complaint updated = complaintRepository.save(complaint);
        logger.info("Complaint ID {} status updated to {} by admin", id, newStatus);

        // Send email notification to complaint owner
        User owner = complaint.getUser();
        emailService.complaintStatusUpdatedEmail(
                owner.getEmail(),
                owner.getName(),
                complaint.getTitle(),
                newStatus.name(),
                request.getAdminReply()
        );

        return mapToResponse(updated);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private ComplaintResponse mapToResponse(Complaint complaint) {
        ComplaintResponse response = new ComplaintResponse();
        response.setId(complaint.getId());
        response.setTitle(complaint.getTitle());
        response.setDescription(complaint.getDescription());
        response.setCategory(complaint.getCategory().name());
        response.setStatus(complaint.getStatus().name());
        response.setAdminReply(complaint.getAdminReply());
        response.setUserName(complaint.getUser().getName());
        response.setUserEmail(complaint.getUser().getEmail());
        response.setCreatedAt(complaint.getCreatedAt());
        response.setUpdatedAt(complaint.getUpdatedAt());
        return response;
    }
}
