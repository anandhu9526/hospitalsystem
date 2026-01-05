package com.healthcare.system.service;

import com.healthcare.system.dto.AppointmentDTO;
import com.healthcare.system.dto.AppointmentRequest;
import com.healthcare.system.dto.PrescriptionRequest;
import java.util.List;

public interface AppointmentService {
    AppointmentDTO bookAppointment(AppointmentRequest request);

    void cancelAppointment(Long appointmentId, String userEmail);

    List<AppointmentDTO> getPatientAppointments(Long patientId);

    List<AppointmentDTO> getDoctorAppointments(Long doctorId);

    void addPrescription(Long appointmentId, PrescriptionRequest request, String doctorEmail);
}
