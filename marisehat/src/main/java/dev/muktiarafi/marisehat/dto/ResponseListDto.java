package dev.muktiarafi.marisehat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ResponseListDto<T> {
    private boolean status;
    private String message;
    private List<T> data;
}
