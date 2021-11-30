package dev.muktiarafi.marisehat.service;


import dev.muktiarafi.marisehat.dto.LabResultDto;
import dev.muktiarafi.marisehat.entity.LabResult;

import java.util.List;
import java.util.UUID;

public interface LabResultService {
    LabResult create(UUID patientId, LabResultDto labResultDto);
    List<LabResult> getPatientLabResult(UUID patientId);
}
