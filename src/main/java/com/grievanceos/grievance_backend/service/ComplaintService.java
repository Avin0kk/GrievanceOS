package com.grievanceos.grievance_backend.service;


import com.grievanceos.grievance_backend.dto.request.CreateComplaintRequest;
import com.grievanceos.grievance_backend.dto.request.UpdateComplaintStatusRequest;
import com.grievanceos.grievance_backend.dto.response.ComplaintResponse;
import com.grievanceos.grievance_backend.dto.response.MapComplaintResponse;
import com.grievanceos.grievance_backend.enums.ComplaintStatus;
import com.grievanceos.grievance_backend.model.Complaint;
import com.grievanceos.grievance_backend.model.User;
import com.grievanceos.grievance_backend.repository.ComplaintRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ComplaintService {

    private final ComplaintRepository complaintRepository;

    public ComplaintResponse createComplaint(CreateComplaintRequest request, UUID citizenId) {
        Point location = null;
        if(request.getLongitude() != null && request.getLatitude() != null) {
            GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
            location = geometryFactory.createPoint(
                    new Coordinate(request.getLongitude(),request.getLatitude())
            );
        }

        Complaint complaint = Complaint.builder()
                .citizenId(citizenId)
                .title(request.getTitle())
                .category(request.getComplaintCategory())
                .description(request.getDescription())
                .location(location)
                .addressText(request.getAddressText())
                .build();

        Complaint savedComplaint = complaintRepository.save(complaint);

        return ComplaintResponse.builder()
                .id(savedComplaint.getId())
                .title(savedComplaint.getTitle())
                .status(savedComplaint.getStatus())
                .priority(savedComplaint.getPriority())
                .createdAt(savedComplaint.getCreatedAt())
                .build();
    }

    public List<ComplaintResponse> getComplaint(UUID citizenId) {
        List<Complaint> complaints = complaintRepository.findByCitizenId(citizenId);

        return complaints.stream()
                .map(c -> ComplaintResponse.builder()
                        .id(c.getId())
                        .title(c.getTitle())
                        .status(c.getStatus())
                        .priority(c.getPriority())
                        .createdAt(c.getCreatedAt())
                        .build()
                )
                .toList();
    }

    public ComplaintResponse getComplaintById(UUID complaintId, UUID citizenId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        if(!complaint.getCitizenId().equals(citizenId)) {
            throw new RuntimeException("Access denied");
        }

        return ComplaintResponse.builder()
                .id(complaint.getId())
                .title(complaint.getTitle())
                .status(complaint.getStatus())
                .priority(complaint.getPriority())
                .createdAt(complaint.getCreatedAt())
                .build();
    }

    public ComplaintResponse updateStatus(UUID complaintId, @NotNull UpdateComplaintStatusRequest request) {

        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        complaint.setStatus(request.getStatus());
        if(request.getStatus() == ComplaintStatus.RESOLVED) {
            complaint.setResolvedAt(ZonedDateTime.now());
        }

        Complaint savedComplaint = complaintRepository.save(complaint);

        return ComplaintResponse.builder()
                .id(savedComplaint.getId())
                .title(savedComplaint.getTitle())
                .status(savedComplaint.getStatus())
                .priority(savedComplaint.getPriority())
                .createdAt(savedComplaint.getCreatedAt())
                .build();
    }

    public List<MapComplaintResponse> getMapComplaint() {
        List<Complaint> complaints = complaintRepository.findByStatus(ComplaintStatus.OPEN);

        return complaints.stream()
                .filter(c -> c.getLocation()!=null)
                .map(c-> MapComplaintResponse.builder()
                        .id(c.getId())
                        .title(c.getTitle())
                        .category(c.getCategory())
                        .status(c.getStatus())
                        .latitude(c.getLocation().getY())
                        .longitude(c.getLocation().getX())
                        .build())
                .toList();
    }
}
