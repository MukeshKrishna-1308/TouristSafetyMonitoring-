package edu.campus.lostfound.web.dto;

public record DashboardResponse(long pendingReports, long matchedPairs, long returnedItems) {
}