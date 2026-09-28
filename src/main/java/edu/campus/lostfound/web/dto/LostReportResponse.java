package edu.campus.lostfound.web.dto;

import edu.campus.lostfound.model.LostReport;
import edu.campus.lostfound.model.LostStatus;
import java.time.Instant;

public record LostReportResponse(Long id, Long userId, String userName, Long categoryId, String category,
        String description, String location, Instant reportDate, LostStatus status) {
    public static LostReportResponse from(LostReport report) {
        return new LostReportResponse(report.getId(), report.getUser().getId(), report.getUser().getName(),
                report.getCategory().getId(), report.getCategory().getName(), report.getDescription(),
                report.getLocation(), report.getReportDate(), report.getStatus());
    }
}