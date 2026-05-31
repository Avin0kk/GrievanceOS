package com.grievanceos.grievance_backend.dto.request;

import com.grievanceos.grievance_backend.enums.ComplaintCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class CreateComplaintRequest {

    @NotNull
    private ComplaintCategory complaintCategory;

    @NotBlank
    private String title;
    private String description;
    private Double latitude;
    private Double longitude;
    private String addressText;

}
