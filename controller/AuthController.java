package com.healthcare.system.controller;

import com.healthcare.system.dto.*;
import com.healthcare.system.entity.Doctor;
import com.healthcare.system.entity.Patient;
import com.healthcare.system.entity.Role;
import com.healthcare.system.entity.User;
import com.healthcare.system.repository.DoctorRepository;
import com.healthcare.system.repository.PatientRepository;
import com.healthcare.system.repository.UserRepository;
import com.healthcare.system.security.JwtUtils;
import com.healthcare.system.security.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), role));
    }

    @PostMapping("/register/patient")
    public ResponseEntity<?> registerPatient(@Valid @RequestBody PatientRegistrationRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        User user = new User(signUpRequest.getEmail(), encoder.encode(signUpRequest.getPassword()), Role.ROLE_PATIENT);
        User savedUser = userRepository.save(user);

        Patient patient = new Patient();
        patient.setName(signUpRequest.getName());
        patient.setEmail(signUpRequest.getEmail());
        patient.setPhone(signUpRequest.getPhone());
        patient.setDob(signUpRequest.getDob());
        patient.setMedicalHistory(signUpRequest.getMedicalHistory());
        patient.setUser(savedUser);
        patientRepository.save(patient);

        return ResponseEntity.ok(new MessageResponse("Patient registered successfully!"));
    }

    @PostMapping("/register/doctor")
    public ResponseEntity<?> registerDoctor(@Valid @RequestBody DoctorRegistrationRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        User user = new User(signUpRequest.getEmail(), encoder.encode(signUpRequest.getPassword()), Role.ROLE_DOCTOR);
        User savedUser = userRepository.save(user);

        Doctor doctor = new Doctor();
        doctor.setName(signUpRequest.getName());
        doctor.setEmail(signUpRequest.getEmail());
        doctor.setPhone(signUpRequest.getPhone());
        doctor.setSpecialization(signUpRequest.getSpecialization());
        doctor.setUser(savedUser);
        doctorRepository.save(doctor);

        return ResponseEntity.ok(new MessageResponse("Doctor registered successfully!"));
    }
}
