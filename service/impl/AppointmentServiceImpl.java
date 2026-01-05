package com.healthcare.system.service.impl;

import com.healthcare.system.dto.AppointmentDTO;
import com.healthcare.system.dto.AppointmentRequest;
import com.healthcare.system.dto.PrescriptionRequest;
import com.healthcare.system.entity.*;
import com.healthcare.system.repository.AppointmentRepository;
import com.healthcare.system.repository.DoctorRepository;
import com.healthcare.system.repository.PatientRepository;
import com.healthcare.system.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Override
    @Transactional
    public AppointmentDTO bookAppointment(AppointmentRequest request) {
        if (request.getAppointmentTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Appointment must be in the future");
        }

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        // Check double booking
        boolean isBooked = appointmentRepository.existsByDoctorIdAndAppointmentTimeAndStatusNot(
                request.getDoctorId(), request.getAppointmentTime(), AppointmentStatus.CANCELLED);
        if (isBooked) {
            throw new RuntimeException("Doctor is already booked at this time");
        }

        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        // Logic to remove from doctor's available slots if managed directly
        doctor.getAvailableSlots().remove(request.getAppointmentTime());
        doctorRepository.save(doctor);

        Appointment saved = appointmentRepository.save(appointment);
        return convertToDTO(saved);
    }

    @Override
    @Transactional
    public void cancelAppointment(Long appointmentId, String userEmail) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Only related doctor or patient can cancel
        if (!appointment.getDoctor().getEmail().equals(userEmail) &&
                !appointment.getPatient().getEmail().equals(userEmail)) {
            throw new RuntimeException("Unauthorized to cancel this appointment");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);

        // Free slots
        Doctor doctor = appointment.getDoctor();
        doctor.getAvailableSlots().add(appointment.getAppointmentTime());
        doctorRepository.save(doctor);

        appointmentRepository.save(appointment);
    }

    @Override
    public List<AppointmentDTO> getPatientAppointments(Long patientId) {
        return appointmentRepository.findByPatientId(patientId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDTO> getDoctorAppointments(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addPrescription(Long appointmentId, PrescriptionRequest request, String doctorEmail) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!appointment.getDoctor().getEmail().equals(doctorEmail)) {
            throw new RuntimeException("Doctors can only add notes to their own appointments");
        }

        // Usually marks as COMPLETED as well
        appointment.setNotes(request.getNotes());
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);
    }

    private AppointmentDTO convertToDTO(Appointment appointment) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setId(appointment.getId());
        dto.setDoctorId(appointment.getDoctor().getId());
        dto.setDoctorName(appointment.getDoctor().getName());
        dto.setPatientId(appointment.getPatient().getId());
        dto.setPatientName(appointment.getPatient().getName());
        dto.setAppointmentTime(appointment.getAppointmentTime());
        dto.setStatus(appointment.getStatus());
        dto.setNotes(appointment.getNotes());
        return dto;
    }
}
