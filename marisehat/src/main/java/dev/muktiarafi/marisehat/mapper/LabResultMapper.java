package dev.muktiarafi.marisehat.mapper;

import dev.muktiarafi.marisehat.dto.LabResultDto;
import dev.muktiarafi.marisehat.entity.LabResult;
import dev.muktiarafi.marisehat.entity.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LabResultMapper {
    @Mapping(target = "blobName", source = "blobName")
    @Mapping(target = "patient", source = "patient")
    LabResult labResultDtoToLabResult(LabResultDto labResultDto, String blobName, Patient patient);
}
