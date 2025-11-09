package org.healthtrack.entity;

import java.time.LocalDate;

public class HealthReport {
    private String reportId;
    private LocalDate reportMonth;  // 对应数据库的report_month
    private Integer totalSteps;
    private String summary;
    private String userId;
    private String verifierId;

    public HealthReport() {}

    public HealthReport(String reportId, LocalDate reportMonth, String userId) {
        this.reportId = reportId;
        this.reportMonth = reportMonth;
        this.userId = userId;
    }

    // getter/setter方法
    public String getReportId() { return reportId; }
    public void setReportId(String reportId) { this.reportId = reportId; }

    public LocalDate getReportMonth() { return reportMonth; }
    public void setReportMonth(LocalDate reportMonth) { this.reportMonth = reportMonth; }

    public Integer getTotalSteps() { return totalSteps; }
    public void setTotalSteps(Integer totalSteps) { this.totalSteps = totalSteps; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getVerifierId() { return verifierId; }
    public void setVerifierId(String verifierId) { this.verifierId = verifierId; }
}