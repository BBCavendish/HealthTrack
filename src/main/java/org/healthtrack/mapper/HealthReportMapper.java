package org.healthtrack.mapper;

import org.apache.ibatis.annotations.*;
import org.healthtrack.entity.HealthReport;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface HealthReportMapper {

    @Select("SELECT * FROM health_report")
    List<HealthReport> findAll();

    @Select("SELECT * FROM health_report WHERE report_id = #{reportId}")
    HealthReport findById(String reportId);

    @Insert("INSERT INTO health_report (report_id, report_month, total_steps, summary, user_id, verifier_id) " +
            "VALUES (#{reportId}, #{reportMonth}, #{totalSteps}, #{summary}, #{userId}, #{verifierId})")
    int insert(HealthReport report);

    @Update("UPDATE health_report SET report_month = #{reportMonth}, total_steps = #{totalSteps}, summary = #{summary}, " +
            "user_id = #{userId}, verifier_id = #{verifierId} WHERE report_id = #{reportId}")
    int update(HealthReport report);

    @Delete("DELETE FROM health_report WHERE report_id = #{reportId}")
    int delete(String reportId);

    @Select("SELECT * FROM health_report WHERE user_id = #{userId}")
    List<HealthReport> findByUserId(String userId);

    @Select("SELECT * FROM health_report WHERE verifier_id = #{verifierId}")
    List<HealthReport> findByVerifierId(String verifierId);

    @Select("SELECT * FROM health_report WHERE report_month BETWEEN #{start} AND #{end}")
    List<HealthReport> findByMonthBetween(LocalDate start, LocalDate end);
}