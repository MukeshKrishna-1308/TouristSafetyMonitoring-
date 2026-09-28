package edu.campus.lostfound.service;

import edu.campus.lostfound.model.Category;
import edu.campus.lostfound.model.FoundItem;
import edu.campus.lostfound.model.FoundStatus;
import edu.campus.lostfound.model.LostReport;
import edu.campus.lostfound.model.LostStatus;
import edu.campus.lostfound.model.User;
import edu.campus.lostfound.model.UserRole;
import edu.campus.lostfound.repository.CategoryRepository;
import edu.campus.lostfound.repository.FoundItemRepository;
import edu.campus.lostfound.repository.LostReportRepository;
import edu.campus.lostfound.web.dto.FoundItemResponse;
import edu.campus.lostfound.web.dto.LostReportRequest;
import edu.campus.lostfound.web.dto.LostReportResponse;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LostReportService {
    private final LostReportRepository reports;
    private final CategoryRepository categories;
    private final FoundItemRepository foundItems;

    public LostReportService(LostReportRepository reports, CategoryRepository categories, FoundItemRepository foundItems) {
        this.reports = reports;
        this.categories = categories;
        this.foundItems = foundItems;
    }

    @Transactional
    public LostReportResponse create(LostReportRequest request, User actor) {
        if (actor.getRole() != UserRole.STUDENT) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only students can submit lost-item reports");
        }
        Category category = categories.findById(request.categoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category does not exist"));
        LostReport report = reports.save(new LostReport(actor, category, request.description().trim(),
                request.location().trim(), request.reportDate() == null ? Instant.now() : request.reportDate()));
        return LostReportResponse.from(report);
    }

    @Transactional(readOnly = true)
    public List<LostReportResponse> list(User actor) {
        List<LostReport> result = actor.getRole() == UserRole.ADMIN || actor.getRole() == UserRole.STAFF
                ? reports.findAllByStatusOrderByReportDateDesc(LostStatus.OPEN)
                : reports.findAllByUser_IdOrderByReportDateDesc(actor.getId());
        return result.stream().map(LostReportResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<FoundItemResponse> matches(long reportId, User actor) {
        LostReport report = reports.findById(reportId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lost report not found"));
        if (actor.getRole() == UserRole.STUDENT && !report.getUser().getId().equals(actor.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view matches for your own report");
        }
        Set<String> keywords = keywords(report.getDescription() + " " + report.getLocation());
        return foundItems.findAllWithCategoryByStatus(FoundStatus.AVAILABLE).stream()
                .filter(item -> item.getCategory().getId().equals(report.getCategory().getId()))
                .map(item -> new Match(item, overlap(keywords, keywords(item.getDescription() + " " + item.getLocation()))))
                .filter(match -> match.score() > 0)
                .sorted((left, right) -> Integer.compare(right.score(), left.score()))
                .map(match -> FoundItemResponse.from(match.item(), match.score()))
                .toList();
    }

    public static int matchScore(LostReport report, FoundItem item) {
        if (!report.getCategory().getId().equals(item.getCategory().getId())) return 0;
        return overlap(keywords(report.getDescription() + " " + report.getLocation()),
            keywords(item.getDescription() + " " + item.getLocation()));
    }

    private static Set<String> keywords(String text) {
        return Arrays.stream(text.toLowerCase(Locale.ROOT).split("[^\\p{L}\\p{N}]+"))
                .filter(word -> word.length() >= 3).collect(Collectors.toSet());
    }

    private static int overlap(Set<String> left, Set<String> right) {
        return (int) left.stream().filter(right::contains).count();
    }

    private record Match(FoundItem item, int score) { }
}