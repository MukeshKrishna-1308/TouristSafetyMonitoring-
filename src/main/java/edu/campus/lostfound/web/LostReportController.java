package edu.campus.lostfound.web;

import edu.campus.lostfound.service.LostReportService;
import edu.campus.lostfound.web.dto.FoundItemResponse;
import edu.campus.lostfound.web.dto.LostReportRequest;
import edu.campus.lostfound.web.dto.LostReportResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lost-reports")
public class LostReportController {
    private final LostReportService reportService;

    public LostReportController(LostReportService reportService) { this.reportService = reportService; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LostReportResponse create(@Valid @RequestBody LostReportRequest request) {
        return reportService.create(request, CurrentUser.get());
    }

    @GetMapping
    public List<LostReportResponse> list() { return reportService.list(CurrentUser.get()); }

    @GetMapping("/{id}/matches")
    public List<FoundItemResponse> matches(@PathVariable long id) {
        return reportService.matches(id, CurrentUser.get());
    }
}