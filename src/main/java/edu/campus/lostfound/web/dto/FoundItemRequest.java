package edu.campus.lostfound.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record FoundItemRequest(
        @NotNull Long categoryId,
        @NotBlank @Size(max = 2000) String description,
        @NotBlank @Size(max = 200) String location,
        @PastOrPresent Instant foundDate) {
}