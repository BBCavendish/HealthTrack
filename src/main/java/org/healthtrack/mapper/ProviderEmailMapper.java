package org.healthtrack.mapper;

import org.apache.ibatis.annotations.*;
import org.healthtrack.entity.ProviderEmail;
import java.util.List;

@Mapper
public interface ProviderEmailMapper {

    @Select("SELECT * FROM provider_email WHERE license_number = #{licenseNumber}")
    List<ProviderEmail> findByProviderId(String licenseNumber);

    @Select("SELECT * FROM provider_email WHERE license_number = #{licenseNumber} AND is_primary = TRUE")
    ProviderEmail findPrimaryEmail(String licenseNumber);

    @Insert("INSERT INTO provider_email (license_number, email_address, is_primary) VALUES (#{licenseNumber}, #{emailAddress}, #{isPrimary})")
    int insert(ProviderEmail providerEmail);

    @Update("UPDATE provider_email SET is_primary = #{isPrimary} WHERE license_number = #{licenseNumber} AND email_address = #{emailAddress}")
    int update(ProviderEmail providerEmail);

    @Delete("DELETE FROM provider_email WHERE license_number = #{licenseNumber} AND email_address = #{emailAddress}")
    int delete(@Param("licenseNumber") String licenseNumber, @Param("emailAddress") String emailAddress);

    @Delete("DELETE FROM provider_email WHERE license_number = #{licenseNumber}")
    int deleteByProviderId(String licenseNumber);

    // 新增：清除所有primary标记的方法
    @Update("UPDATE provider_email SET is_primary = FALSE WHERE license_number = #{licenseNumber}")
    int clearPrimaryFlags(String licenseNumber);

    // 新增：通过邮箱地址查找ProviderEmail
    @Select("SELECT * FROM provider_email WHERE email_address = #{emailAddress}")
    List<ProviderEmail> findByEmailAddress(String emailAddress);
}