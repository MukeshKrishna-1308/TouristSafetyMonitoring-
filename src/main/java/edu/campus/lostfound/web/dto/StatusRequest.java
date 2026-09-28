package edu.campus.lostfound.web.dto;

import edu.campus.lostfound.model.FoundStatus;
import jakarta.validation.constraints.NotNull;

public record StatusRequest(@NotNull FoundStatus status) {
}