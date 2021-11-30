package dev.muktiarafi.marisehat.repository;

import dev.muktiarafi.marisehat.entity.Patient;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface PatientRepository extends PagingAndSortingRepository<Patient, UUID> {
}
