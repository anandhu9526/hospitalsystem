package com.healthcare.system.service.impl;

import com.healthcare.system.dto.DoctorDTO;
import com.healthcare.system.entity.Doctor;
import com.healthcare.system.repository.DoctorRepository;
import com.healthcare.system.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Override
    public Page<DoctorDTO> searchDoctors(String specialization, Pageable pageable) {
        Page<Doctor> doctors;
        if (specialization != null && !specialization.isEmpty()) {
            doctors = doctorRepository.findBySpecializationContainingIgnoreCase(specialization, pageable);
        } else {
            doctors = doctorRepository.findAll(pageable);
        }
        return doctors.map(this::convertToDTO);
    }

    @Override
    public List<LocalDateTime> getDoctorAvailability(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        return doctor.getAvailableSlots();
    }

    private DoctorDTO convertToDTO(Doctor doctor) {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(doctor.getId());
        dto.setName(doctor.getName());
        dto.setSpecialization(doctor.getSpecialization());
        dto.setEmail(doctor.getEmail());
        dto.setPhone(doctor.getPhone());
        dto.setAvailableSlots(doctor.getAvailableSlots());
        return dto;
    }
}
