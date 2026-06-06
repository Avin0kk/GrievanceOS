package com.grievanceos.grievance_backend.dto.response;

public record NotificationMessage(String title,
                                 String message,
                                 String complaintId
                                 ) {}
