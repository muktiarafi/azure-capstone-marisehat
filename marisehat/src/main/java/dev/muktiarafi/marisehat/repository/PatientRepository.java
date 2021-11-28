package dev.muktiarafi.marisehat.repository;

import dev.muktiarafi.marisehat.entity.Patient;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PatientRepository extends PagingAndSortingRepository<Patient, Long> {
}
