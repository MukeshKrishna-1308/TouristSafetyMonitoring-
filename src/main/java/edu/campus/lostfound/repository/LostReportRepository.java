package edu.campus.lostfound.repository;

import edu.campus.lostfound.model.LostReport;
import edu.campus.lostfound.model.LostStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LostReportRepository extends JpaRepository<LostReport, Long> {
    List<LostReport> findAllByUser_IdOrderByReportDateDesc(Long userId);
    List<LostReport> findAllByStatusOrderByReportDateDesc(LostStatus status);
    long countByStatus(LostStatus status);
}