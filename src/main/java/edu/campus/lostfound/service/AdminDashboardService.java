package edu.campus.lostfound.service;

import edu.campus.lostfound.model.FoundItem;
import edu.campus.lostfound.model.FoundStatus;
import edu.campus.lostfound.model.LostReport;
import edu.campus.lostfound.model.LostStatus;
import edu.campus.lostfound.model.User;
import edu.campus.lostfound.model.UserRole;
import edu.campus.lostfound.repository.FoundItemRepository;
import edu.campus.lostfound.repository.LostReportRepository;
import edu.campus.lostfound.web.dto.DashboardResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AdminDashboardService {
    private final LostReportRepository reports;
    private final FoundItemRepository foundItems;

    public AdminDashboardService(LostReportRepository reports, FoundItemRepository foundItems) {
        this.reports = reports;
        this.foundItems = foundItems;
    }

    @Transactional(readOnly = true)
    public DashboardResponse getStats(User actor) {
        if (actor.getRole() != UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Administrator role required");
        }
        var availableItems = foundItems.findAllWithCategoryByStatus(FoundStatus.AVAILABLE);
        long matchedPairs = reports.findAllByStatusOrderByReportDateDesc(LostStatus.OPEN).stream()
            .mapToLong(report -> availableItems.stream()
                .filter(item -> LostReportService.matchScore(report, item) > 0).count())
                .sum();
        return new DashboardResponse(reports.countByStatus(LostStatus.OPEN), matchedPairs,
                foundItems.countByStatus(FoundStatus.RETURNED));
    }
}