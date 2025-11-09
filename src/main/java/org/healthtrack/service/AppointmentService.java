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
}