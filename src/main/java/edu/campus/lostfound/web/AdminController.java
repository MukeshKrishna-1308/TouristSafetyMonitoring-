package edu.campus.lostfound.web;

import edu.campus.lostfound.service.AdminDashboardService;
import edu.campus.lostfound.web.dto.DashboardResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminDashboardService dashboardService;

    public AdminController(AdminDashboardService dashboardService) { this.dashboardService = dashboardService; }

    @GetMapping("/dashboard")
    public DashboardResponse dashboard() { return dashboardService.getStats(CurrentUser.get()); }
}