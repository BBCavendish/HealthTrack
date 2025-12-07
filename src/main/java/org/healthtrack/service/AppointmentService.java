package org.healthtrack.service;

import org.healthtrack.entity.Appointment;
import java.util.List;

public interface AppointmentService {
    List<Appointment> getAllAppointments();
    Appointment getAppointmentById(String appointmentId);
    boolean saveAppointment(Appointment appointment);
    boolean deleteAppointment(String appointmentId);
    List<Appointment> getAppointmentsByUser(String userId);
    List<Appointment> getAppointmentsByStatus(String status);
    boolean cancelAppointment(String appointmentId, String reason);
    boolean linkProviderToAppointment(String appointmentId, String licenseNumber);
    List<Appointment> getAppointmentsByProvider(String licenseNumber);
    
    /**
     * 多条件搜索预约
     * @param userId 用户ID（可选）
     * @param status 状态（可选）
     * @param type 类型（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @param providerLicense 提供者许可证号（可选）
     * @return 符合条件的预约列表
     */
    List<Appointment> searchAppointments(String userId, String status, String type, 
                                         java.time.LocalDateTime startDate, 
                                         java.time.LocalDateTime endDate, 
                                         String providerLicense);
}