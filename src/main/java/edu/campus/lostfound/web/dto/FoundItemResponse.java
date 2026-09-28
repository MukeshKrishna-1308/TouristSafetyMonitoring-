package edu.campus.lostfound.web.dto;

import edu.campus.lostfound.model.FoundItem;
import edu.campus.lostfound.model.FoundStatus;
import java.time.Instant;

public record FoundItemResponse(Long id, Long staffId, String staffName, Long categoryId, String category,
        String description, String location, Instant foundDate, FoundStatus status, int matchScore) {
    public static FoundItemResponse from(FoundItem item) {
        return from(item, 0);
    }

    public static FoundItemResponse from(FoundItem item, int matchScore) {
        return new FoundItemResponse(item.getId(), item.getStaff().getId(), item.getStaff().getName(),
                item.getCategory().getId(), item.getCategory().getName(), item.getDescription(),
                item.getLocation(), item.getFoundDate(), item.getStatus(), matchScore);
    }
}