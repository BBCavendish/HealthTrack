package org.healthtrack.mapper;

import org.apache.ibatis.annotations.*;
import org.healthtrack.entity.Appointment;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AppointmentMapper {

    @Select("SELECT * FROM appointment")
    List<Appointment> findAll();

    @Select("SELECT * FROM appointment WHERE appointment_id = #{appointmentId}")
    Appointment findById(String appointmentId);

    @Insert("INSERT INTO appointment (appointment_id, date_time, type, note, status, cancel_reason, user_id, report_id) " +
            "VALUES (#{appointmentId}, #{dateTime}, #{type}, #{note}, #{status}, #{cancelReason}, #{userId}, #{reportId})")
    int insert(Appointment appointment);

    @Update("UPDATE appointment SET date_time = #{dateTime}, type = #{type}, note = #{note}, status = #{status}, " +
            "cancel_reason = #{cancelReason}, user_id = #{userId}, report_id = #{reportId} " +
            "WHERE appointment_id = #{appointmentId}")
    int update(Appointment appointment);

    @Delete("DELETE FROM appointment WHERE appointment_id = #{appointmentId}")
    int delete(String appointmentId);

    @Select("SELECT * FROM appointment WHERE user_id = #{userId}")
    List<Appointment> findByUserId(String userId);

    @Select("SELECT * FROM appointment WHERE date_time BETWEEN #{start} AND #{end}")
    List<Appointment> findByDateTimeBetween(LocalDateTime start, LocalDateTime end);

    @Select("SELECT * FROM appointment WHERE status = #{status}")
    List<Appointment> findByStatus(String status);

    @Select("SELECT a.* FROM appointment a " +
            "JOIN appointment_provider ap ON a.appointment_id = ap.appointment_id " +
            "WHERE ap.license_number = #{licenseNumber}")
    List<Appointment> findByProviderId(String licenseNumber);

    @Select("<script>" +
            "SELECT * FROM appointment WHERE 1=1 " +
            "<if test='userId != null'> AND user_id = #{userId} </if>" +
            "<if test='status != null and status != \"\"'> AND status = #{status} </if>" +
            "<if test='type != null and type != \"\"'> AND type = #{type} </if>" +
            "<if test='startDate != null'> AND date_time &gt;= #{startDate} </if>" +
            "<if test='endDate != null'> AND date_time &lt;= #{endDate} </if>" +
            "<if test='providerLicense != null and providerLicense != \"\"'> " +
            "AND appointment_id IN (SELECT appointment_id FROM appointment_provider WHERE license_number = #{providerLicense}) " +
            "</if>" +
            "ORDER BY date_time DESC" +
            "</script>")
    List<Appointment> searchAppointments(@Param("userId") String userId, 
                                         @Param("status") String status, 
                                         @Param("type") String type, 
                                         @Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate, 
                                         @Param("providerLicense") String providerLicense);
}