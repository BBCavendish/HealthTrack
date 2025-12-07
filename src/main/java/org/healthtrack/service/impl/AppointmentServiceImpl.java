package org.healthtrack.service.impl;

import org.healthtrack.entity.Appointment;
import org.healthtrack.entity.AppointmentProvider;
import org.healthtrack.mapper.AppointmentMapper;
import org.healthtrack.mapper.AppointmentProviderMapper;
import org.healthtrack.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private AppointmentProviderMapper appointmentProviderMapper;

    @Override
    public List<Appointment> getAllAppointments() {
        try {
            return appointmentMapper.findAll();
        } catch (Exception e) {
            System.err.println("获取预约列表失败: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public Appointment getAppointmentById(String appointmentId) {
        try {
            return appointmentMapper.findById(appointmentId);
        } catch (Exception e) {
            System.err.println("获取预约失败: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean saveAppointment(Appointment appointment) {
        try {
            Appointment existing = appointmentMapper.findById(appointment.getAppointmentId());
            if (existing != null) {
                return appointmentMapper.update(appointment) > 0;
            } else {
                return appointmentMapper.insert(appointment) > 0;
            }
        } catch (Exception e) {
            System.err.println("保存预约失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteAppointment(String appointmentId) {
        try {
            return appointmentMapper.delete(appointmentId) > 0;
        } catch (Exception e) {
            System.err.println("删除预约失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Appointment> getAppointmentsByUser(String userId) {
        try {
            return appointmentMapper.findByUserId(userId);
        } catch (Exception e) {
            System.err.println("获取用户预约失败: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<Appointment> getAppointmentsByStatus(String status) {
        try {
            return appointmentMapper.findByStatus(status);
        } catch (Exception e) {
            System.err.println("获取状态预约失败: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public boolean cancelAppointment(String appointmentId, String reason) {
        try {
            Appointment appointment = appointmentMapper.findById(appointmentId);
            if (appointment == null) {
                return false;
            }
            appointment.setStatus("Cancelled");
            appointment.setCancelReason(reason);
            return appointmentMapper.update(appointment) > 0;
        } catch (Exception e) {
            System.err.println("取消预约失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean linkProviderToAppointment(String appointmentId, String licenseNumber) {
        try {
            AppointmentProvider appointmentProvider = new AppointmentProvider(appointmentId, licenseNumber);
            appointmentProvider.setLinkedBy("system"); // 可以设置为当前用户
            return appointmentProviderMapper.insert(appointmentProvider) > 0;
        } catch (Exception e) {
            System.err.println("关联提供者失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Appointment> getAppointmentsByProvider(String licenseNumber) {
        try {
            return appointmentMapper.findByProviderId(licenseNumber);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public List<Appointment> searchAppointments(String userId, String status, String type, 
                                                java.time.LocalDateTime startDate, 
                                                java.time.LocalDateTime endDate, 
                                                String providerLicense) {
        try {
            return appointmentMapper.searchAppointments(userId, status, type, startDate, endDate, providerLicense);
        } catch (Exception e) {
            System.err.println("搜索预约失败: " + e.getMessage());
            return List.of();
        }
    }
}