package com.healthcare.system.repository;

import com.healthcare.system.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Page<Doctor> findBySpecializationContainingIgnoreCase(String specialization, Pageable pageable);
}
