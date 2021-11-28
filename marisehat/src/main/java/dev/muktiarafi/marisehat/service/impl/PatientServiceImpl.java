package dev.muktiarafi.marisehat.service.impl;

import dev.muktiarafi.marisehat.dto.PatientDto;
import dev.muktiarafi.marisehat.entity.Patient;
import dev.muktiarafi.marisehat.exception.ResourceNotFoundException;
import dev.muktiarafi.marisehat.mapper.PatientMapper;
import dev.muktiarafi.marisehat.repository.PatientRepository;
import dev.muktiarafi.marisehat.service.PatientService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    @Override
    public Patient create(PatientDto patientDto) {
        var patient = patientMapper.patientDtoToPatient(patientDto);

        return patientRepository.save(patient);
    }

    @Override
    public Patient get(Long patientId) {
        var patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        return patient;
    }

    @Override
    public Page<Patient> getAll(int page, int size) {
        var paging = PageRequest.of(page, size);

        return patientRepository.findAll(paging);
    }

    @Override
    public Patient update(Long patientId, PatientDto patientDto) {
        var patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        patientMapper.fromDto(patientDto, patient);

        return patientRepository.save(patient);
    }

    @Override
    public Patient delete(Long patientId) {
        var patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        patientRepository.delete(patient);

        return patient;
    }
}
