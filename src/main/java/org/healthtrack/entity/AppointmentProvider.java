package org.healthtrack.entity;

import java.time.LocalDateTime;

/**
 * 预约-提供者关联实体类
 * 表示医疗提供者与预约的关联关系
 */
public class AppointmentProvider {
    private String appointmentId;      // 预约ID
    private String licenseNumber;      // 医疗提供者许可证号
    private LocalDateTime linkedTime;  // 关联时间
    private String linkedBy;           // 关联操作人
    private String note;               // 备注信息

    // 默认构造方法
    public AppointmentProvider() {
        this.linkedTime = LocalDateTime.now();
    }

    // 带参数的构造方法
    public AppointmentProvider(String appointmentId, String licenseNumber) {
        this.appointmentId = appointmentId;
        this.licenseNumber = licenseNumber;
        this.linkedTime = LocalDateTime.now();
    }

    // Getter和Setter方法
    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public LocalDateTime getLinkedTime() {
        return linkedTime;
    }

    public void setLinkedTime(LocalDateTime linkedTime) {
        this.linkedTime = linkedTime;
    }

    public String getLinkedBy() {
        return linkedBy;
    }

    public void setLinkedBy(String linkedBy) {
        this.linkedBy = linkedBy;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "AppointmentProvider{" +
                "appointmentId='" + appointmentId + '\'' +
                ", licenseNumber='" + licenseNumber + '\'' +
                ", linkedTime=" + linkedTime +
                ", linkedBy='" + linkedBy + '\'' +
                ", note='" + note + '\'' +
                '}';
    }

    // 重写equals和hashCode方法
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppointmentProvider that = (AppointmentProvider) o;

        if (appointmentId != null ? !appointmentId.equals(that.appointmentId) : that.appointmentId != null)
            return false;
        return licenseNumber != null ? licenseNumber.equals(that.licenseNumber) : that.licenseNumber == null;
    }

    @Override
    public int hashCode() {
        int result = appointmentId != null ? appointmentId.hashCode() : 0;
        result = 31 * result + (licenseNumber != null ? licenseNumber.hashCode() : 0);
        return result;
    }
}