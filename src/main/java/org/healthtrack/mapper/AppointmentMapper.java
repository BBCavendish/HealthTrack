package org.healthtrack.mapper;

import org.apache.ibatis.annotations.*;
import org.healthtrack.entity.Appointment;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AppointmentMapper {

    @Select("SELECT * FROM appointment")
    List<Appointment> findAll();

    @Select("SELECT * FROM appointment WHERE appointmentid = #{appointmentId}")
    Appointment findById(String appointmentId);

    @Insert("INSERT INTO appointment (appointmentid, datetime, type, note, status, cancelreason, userid, reportid) " +
            "VALUES (#{appointmentId}, #{dateTime}, #{type}, #{note}, #{status}, #{cancelReason}, #{user.healthId}, #{report.reportId})")
    int insert(Appointment appointment);

    @Update("UPDATE appointment SET datetime = #{dateTime}, type = #{type}, note = #{note}, status = #{status}, " +
            "cancelreason = #{cancelReason}, userid = #{user.healthId}, reportid = #{report.reportId} " +
            "WHERE appointmentid = #{appointmentId}")
    int update(Appointment appointment);

    @Delete("DELETE FROM appointment WHERE appointmentid = #{appointmentId}")
    int delete(String appointmentId);

    @Select("SELECT * FROM appointment WHERE userid = #{userId}")
    List<Appointment> findByUserId(String userId);

    @Select("SELECT * FROM appointment WHERE datetime BETWEEN #{start} AND #{end}")
    List<Appointment> findByDateTimeBetween(LocalDateTime start, LocalDateTime end);

    @Select("SELECT * FROM appointment WHERE status = #{status}")
    List<Appointment> findByStatus(String status);

    @Select("SELECT a.* FROM appointment a " +
            "JOIN appointment_provider ap ON a.appointment_id = ap.appointment_id " +
            "WHERE ap.license_number = #{licenseNumber}")
    List<Appointment> findByProviderId(String licenseNumber);
}