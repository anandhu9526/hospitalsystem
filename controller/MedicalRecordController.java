package com.healthcare.system.controller;

import com.healthcare.system.dto.AppointmentDTO;
import com.healthcare.system.dto.MessageResponse;
import com.healthcare.system.dto.PrescriptionRequest;
import com.healthcare.system.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
public class MedicalRecordController {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping("/appointments/{id}/prescription")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<MessageResponse> addPrescription(
            @PathVariable Long id,
            @Valid @RequestBody PrescriptionRequest request,
            Principal principal) {
        appointmentService.addPrescription(id, request, principal.getName());
        return ResponseEntity.ok(new MessageResponse("Prescription added successfully"));
    }

    @GetMapping("/medical-records/patient/{patientId}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR')")
    public ResponseEntity<List<AppointmentDTO>> getMedicalRecords(@PathVariable Long patientId) {
        // In this simple system, medical records are just the list of completed
        // appointments
        List<AppointmentDTO> records = appointmentService.getPatientAppointments(patientId);
        return ResponseEntity.ok(records);
    }
}
