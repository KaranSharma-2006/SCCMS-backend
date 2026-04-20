package com.smartcity.controller;

import com.smartcity.dto.request.ComplaintRequest;
import com.smartcity.dto.request.ComplaintStatusUpdateRequest;
import com.smartcity.dto.response.ApiResponse;
import com.smartcity.dto.response.ComplaintResponse;
import com.smartcity.service.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createComplaint(@Valid @RequestBody ComplaintRequest request) {
        ComplaintResponse response = complaintService.createComplaint(request);
        return new ResponseEntity<>(ApiResponse.success("Complaint submitted successfully", response), HttpStatus.CREATED);
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse> getMyComplaints() {
        List<ComplaintResponse> responses = complaintService.getMyComplaints();
        return ResponseEntity.ok(ApiResponse.success("User complaints retrieved successfully", responses));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getAllComplaints() {
        List<ComplaintResponse> responses = complaintService.getAllComplaints();
        return ResponseEntity.ok(ApiResponse.success("All complaints retrieved successfully", responses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getComplaintById(@PathVariable Long id) {
        ComplaintResponse response = complaintService.getComplaintById(id);
        return ResponseEntity.ok(ApiResponse.success("Complaint details retrieved successfully", response));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> updateComplaintStatus(
            @PathVariable Long id,
            @Valid @RequestBody ComplaintStatusUpdateRequest request) {
        ComplaintResponse response = complaintService.updateComplaintStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Complaint status updated successfully", response));
    }
}
