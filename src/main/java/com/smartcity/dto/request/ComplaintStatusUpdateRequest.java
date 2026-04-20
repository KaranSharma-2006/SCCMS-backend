package com.smartcity.dto.request;

import jakarta.validation.constraints.NotNull;

public class ComplaintStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private String status;

    private String adminReply;

    public ComplaintStatusUpdateRequest() {
    }

    public ComplaintStatusUpdateRequest(String status, String adminReply) {
        this.status = status;
        this.adminReply = adminReply;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAdminReply() {
        return adminReply;
    }

    public void setAdminReply(String adminReply) {
        this.adminReply = adminReply;
    }
}
