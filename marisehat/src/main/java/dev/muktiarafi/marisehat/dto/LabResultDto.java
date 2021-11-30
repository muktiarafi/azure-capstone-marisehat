package dev.muktiarafi.marisehat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LabResultDto {
    @Min(0)
    private double hemogoblin;

    @Min(0)
    private double entrosit;

    @Min(0)
    private double leukosit;

    @Min(0)
    private double trombosit;

    @Min(0)
    private double hematokrit;

    @NotNull
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date conductedAt;

    List<String> notes;
}
