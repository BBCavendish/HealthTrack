package org.healthtrack.entity;

import java.time.LocalDateTime;

public class Appointment {
    private String appointmentId;
    private LocalDateTime dateTime;
    private String type;
    private String note;
    private String status;
    private String cancelReason;
    private String userId;
    private String reportId;

    public Appointment() {}

    public Appointment(String appointmentId, LocalDateTime dateTime, String userId) {
        this.appointmentId = appointmentId;
        this.dateTime = dateTime;
        this.userId = userId;
    }

    // getter/setter方法
    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getReportId() { return reportId; }
    public void setReportId(String reportId) { this.reportId = reportId; }
}