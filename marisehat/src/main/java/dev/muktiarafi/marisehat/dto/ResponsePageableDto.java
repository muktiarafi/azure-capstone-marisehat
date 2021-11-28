package dev.muktiarafi.marisehat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ResponsePageableDto<T> {
    private boolean status;
    private String message;
    private int totalItems;
    private int currentPage;
    private int totalPages;
    private List<T> data;
}
