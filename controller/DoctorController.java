package com.healthcare.system.controller;

import com.healthcare.system.dto.DoctorDTO;
import com.healthcare.system.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @GetMapping
    public ResponseEntity<Page<DoctorDTO>> searchDoctors(
            @RequestParam(required = false) String specialization,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(doctorService.searchDoctors(specialization, pageable));
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<List<LocalDateTime>> getDoctorAvailability(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.getDoctorAvailability(id));
    }
}
