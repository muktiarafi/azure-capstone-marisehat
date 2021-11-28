package dev.muktiarafi.marisehat.mapper;

import dev.muktiarafi.marisehat.dto.PatientDto;
import dev.muktiarafi.marisehat.entity.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    Patient patientDtoToPatient(PatientDto patientDto);
    void fromDto(PatientDto patientDto, @MappingTarget Patient patient);
}
