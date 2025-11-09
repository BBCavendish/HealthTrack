package org.healthtrack.mapper;

import org.apache.ibatis.annotations.*;
import org.healthtrack.entity.AppointmentProvider;
import java.util.List;

@Mapper
public interface AppointmentProviderMapper {

    @Insert("INSERT INTO appointment_provider (appointment_id, license_number, linked_time, linked_by, note) " +
            "VALUES (#{appointmentId}, #{licenseNumber}, #{linkedTime}, #{linkedBy}, #{note})")
    int insert(AppointmentProvider appointmentProvider);

    @Delete("DELETE FROM appointment_provider WHERE appointment_id = #{appointmentId} AND license_number = #{licenseNumber}")
    int delete(@Param("appointmentId") String appointmentId, @Param("licenseNumber") String licenseNumber);

    @Select("SELECT * FROM appointment_provider WHERE appointment_id = #{appointmentId}")
    List<AppointmentProvider> findByAppointmentId(String appointmentId);

    @Select("SELECT * FROM appointment_provider WHERE license_number = #{licenseNumber}")
    List<AppointmentProvider> findByLicenseNumber(String licenseNumber);

    @Select("SELECT * FROM appointment_provider WHERE appointment_id = #{appointmentId} AND license_number = #{licenseNumber}")
    AppointmentProvider findByAppointmentAndProvider(@Param("appointmentId") String appointmentId,
                                                     @Param("licenseNumber") String licenseNumber);

    @Update("UPDATE appointment_provider SET note = #{note}, linked_by = #{linkedBy} " +
            "WHERE appointment极_id = #{appointmentId} AND license_number = #{licenseNumber}")
    int updateNote(@Param("appointmentId") String appointmentId,
                   @Param("licenseNumber") String licenseNumber,
                   @Param("note") String note,
                   @Param("linkedBy") String linkedBy);

    @Select("SELECT COUNT(*) FROM appointment_provider WHERE appointment_id = #{appointmentId}")
    int countByAppointmentId(String appointmentId);

    @Select("SELECT COUNT(*) FROM appointment_provider WHERE license_number = #{licenseNumber}")
    int countByLicenseNumber(String licenseNumber);
}