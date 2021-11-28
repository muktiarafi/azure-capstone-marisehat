package dev.muktiarafi.marisehat.service;

import dev.muktiarafi.marisehat.dto.PatientDto;
import dev.muktiarafi.marisehat.entity.Patient;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PatientService {
    Patient create(PatientDto patientDto);
    Patient get(Long patientId);
    Page<Patient> getAll(int page, int size);
    Patient update(Long patientId, PatientDto patientDto);
    Patient delete(Long patientId);
}
