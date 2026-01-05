package com.healthcare.system.service;

import com.healthcare.system.dto.DoctorDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

public interface DoctorService {
    Page<DoctorDTO> searchDoctors(String specialization, Pageable pageable);

    List<LocalDateTime> getDoctorAvailability(Long doctorId);
}
