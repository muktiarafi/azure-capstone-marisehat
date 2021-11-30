package dev.muktiarafi.marisehat.repository;

import dev.muktiarafi.marisehat.entity.LabResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LabResultRepository extends JpaRepository<LabResult, UUID> {
    List<LabResult> findByPatientId(UUID patientId);
}
