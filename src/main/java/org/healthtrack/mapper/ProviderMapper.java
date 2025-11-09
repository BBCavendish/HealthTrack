package org.healthtrack.mapper;

import org.apache.ibatis.annotations.*;
import org.healthtrack.entity.Provider;
import java.util.List;

@Mapper
public interface ProviderMapper {

    @Select("SELECT * FROM provider")
    List<Provider> findAll();

    @Select("SELECT * FROM provider WHERE license_number = #{licenseNumber}")
    Provider findById(String licenseNumber);

    @Insert("INSERT INTO provider (license_number, name, specialty, email, verified_status, phone) " +
            "VALUES (#{licenseNumber}, #{name}, #{specialty}, #{email}, #{verifiedStatus}, #{phone})")
    int insert(Provider provider);

    @Update("UPDATE provider SET name = #{name}, specialty = #{specialty}, email = #{email}, " +
            "verified_status = #{verifiedStatus}, phone = #{phone} WHERE license_number = #{licenseNumber}")
    int update(Provider provider);

    @Delete("DELETE FROM provider WHERE license_number = #{licenseNumber}")
    int delete(String licenseNumber);

    @Select("SELECT * FROM provider WHERE name LIKE CONCAT('%', #{name}, '%')")
    List<Provider> findByNameContaining(String name);

    @Select("SELECT * FROM provider WHERE specialty = #{specialty}")
    List<Provider> findBySpecialty(String specialty);

    @Select("SELECT * FROM provider WHERE verified_status = #{verifiedStatus}")
    List<Provider> findByVerifiedStatus(String verifiedStatus);

    @Select("SELECT * FROM provider WHERE email = #{email}")
    Provider findByEmail(String email);
}