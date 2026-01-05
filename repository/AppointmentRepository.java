package com.healthcare.system.repository;

import com.healthcare.system.entity.Appointment;
import com.healthcare.system.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByDoctorId(Long doctorId);

    boolean existsByDoctorIdAndAppointmentTimeAndStatusNot(Long doctorId, java.time.LocalDateTime time,
            AppointmentStatus status);
}
