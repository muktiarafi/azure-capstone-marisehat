package dev.muktiarafi.marisehat.controller;

import dev.muktiarafi.marisehat.dto.LabResultDto;
import dev.muktiarafi.marisehat.dto.PatientDto;
import dev.muktiarafi.marisehat.dto.ResponseDto;
import dev.muktiarafi.marisehat.dto.ResponseListDto;
import dev.muktiarafi.marisehat.dto.ResponsePageableDto;
import dev.muktiarafi.marisehat.entity.LabResult;
import dev.muktiarafi.marisehat.entity.Patient;
import dev.muktiarafi.marisehat.service.LabResultService;
import dev.muktiarafi.marisehat.service.PatientService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/patients")
@AllArgsConstructor
public class PatientController {
    private final PatientService patientService;
    private final LabResultService labResultService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto<Patient> create(@RequestBody PatientDto patientDto) {
        var patient = patientService.create(patientDto);

        return ResponseDto.<Patient>builder()
                .status(true)
                .message(HttpStatus.CREATED.getReasonPhrase())
                .data(patient)
                .build();
    }

    @PutMapping("/{patientId}")
    public ResponseDto<Patient> update(@PathVariable UUID patientId,  @RequestBody PatientDto patientDto) {
        var patient = patientService.update(patientId, patientDto);

        return ResponseDto.<Patient>builder()
                .status(true)
                .message(HttpStatus.CREATED.getReasonPhrase())
                .data(patient)
                .build();
    }


    @GetMapping("/{patientId}")
    public ResponseDto<Patient> get(@PathVariable UUID patientId) {
        var patient = patientService.get(patientId);

        return ResponseDto.<Patient>builder()
                .status(true)
                .message(HttpStatus.OK.getReasonPhrase())
                .data(patient)
                .build();
    }

    @GetMapping
    public ResponsePageableDto<Patient> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        var patients = patientService.getAll(page, size);

        return ResponsePageableDto.<Patient>builder()
                .status(true)
                .message(HttpStatus.OK.getReasonPhrase())
                .totalItems(patients.getSize())
                .currentPage(patients.getNumber())
                .totalPages(patients.getTotalPages())
                .data(patients.getContent())
                .build();
    }

    @DeleteMapping("/{patientId}")
    public ResponseDto<Patient> delete(@PathVariable UUID patientId) {
        var patient = patientService.delete(patientId);

        return ResponseDto.<Patient>builder()
                .status(true)
                .message(HttpStatus.OK.getReasonPhrase())
                .data(patient)
                .build();
    }

    @PostMapping("/{patientId}/lab-results")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto<LabResult> createLabResult(@PathVariable UUID patientId, @RequestBody LabResultDto labResultDto) {
        var labResult = labResultService.create(patientId, labResultDto);

        return ResponseDto.<LabResult>builder()
                .status(true)
                .message(HttpStatus.CREATED.getReasonPhrase())
                .data(labResult)
                .build();
    }

    @GetMapping("/{patientId}/lab-results")
    public ResponseListDto<LabResult> getPatientLabResult(@PathVariable UUID patientId) {
        var labResults = labResultService.getPatientLabResult(patientId);

        return ResponseListDto.<LabResult>builder()
                .status(true)
                .message(HttpStatus.OK.getReasonPhrase())
                .data(labResults)
                .build();
    }
}
