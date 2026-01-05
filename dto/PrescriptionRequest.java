package com.healthcare.system.dto;

import jakarta.validation.constraints.NotBlank;

public class PrescriptionRequest {
    @NotBlank
    private String notes;

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
