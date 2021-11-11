package dev.muktiarafi.marisehat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @NotBlank
    @Size(min = 4, max = 255)
    private String name;

    @NotBlank
    @Size(min = 4, max = 45)
    private String nickname;

    @NotBlank
    @Size(min = 12)
    private String password;
}
