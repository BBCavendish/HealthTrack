package org.healthtrack.service;

import org.healthtrack.dto.HealthIndicatorStats;
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
    
    /**
     * 获取指定月份的健康指标统计（平均值/最小值/最大值）
     * @param userId 用户ID
     * @param month 月份（格式：yyyy-MM）
     * @param indicatorType 指标类型：weight（体重）、blood_pressure_systolic（收缩压）、blood_pressure_diastolic（舒张压）
     * @return 健康指标统计结果
     */
    HealthIndicatorStats getMonthlyIndicatorStats(String userId, String month, String indicatorType);
}