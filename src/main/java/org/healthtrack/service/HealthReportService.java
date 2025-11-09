package org.healthtrack.service;

import org.healthtrack.entity.HealthReport;
import java.time.LocalDate;
import java.util.List;

public interface HealthReportService {
    List<HealthReport> getAllReports();
    HealthReport getReportById(String reportId);
    boolean saveReport(HealthReport report);
    boolean deleteReport(String reportId);
    List<HealthReport> getReportsByUser(String userId);
    List<HealthReport> getReportsByDateRange(LocalDate start, LocalDate end);
    int countReportsByUser(String userId);
    boolean verifyReport(String reportId, String verifierId);
}