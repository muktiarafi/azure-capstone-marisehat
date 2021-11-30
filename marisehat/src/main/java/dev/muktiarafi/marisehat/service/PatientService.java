package dev.muktiarafi.marisehat.service;

import dev.muktiarafi.marisehat.dto.PatientDto;
import dev.muktiarafi.marisehat.entity.Patient;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface PatientService {
    Patient create(PatientDto patientDto);
    Patient get(UUID patientId);
    Page<Patient> getAll(int page, int size);
    Patient update(UUID patientId, PatientDto patientDto);
    Patient delete(UUID patientId);
}
