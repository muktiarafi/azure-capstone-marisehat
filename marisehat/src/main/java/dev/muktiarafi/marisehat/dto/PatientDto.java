package dev.muktiarafi.marisehat.dto;

import dev.muktiarafi.marisehat.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientDto {
    @NotBlank
    @Size(min = 4, max = 255)
    private String fullName;

    @Min(0)
    private Integer age;

    private Gender gender;

    @NotBlank
    @Size(min = 10)
    private String phoneNumber;

    @NotBlank
    @Size(min = 5, max = 255)
    private String address;
}
