package org.healthtrack.mapper;

import org.apache.ibatis.annotations.*;
import org.healthtrack.entity.User;
import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM app_user")
    List<User> findAll();

    @Select("SELECT * FROM app_user WHERE health_id = #{healthId}")
    User findById(String healthId);

    @Insert("INSERT INTO app_user (health_id, name, phone, email, verification_status, role, family_id) " +
            "VALUES (#{healthId}, #{name}, #{phone}, #{email}, #{verificationStatus}, #{role}, #{familyId})")
    int insert(User user);

    @Update("UPDATE app_user SET name = #{name}, phone = #{phone}, email = #{email}, " +
            "verification_status = #{verificationStatus}, role = #{role}, family_id = #{familyId} " +
            "WHERE health_id = #{healthId}")
    int update(User user);

    @Delete("DELETE FROM app_user WHERE health_id = #{healthId}")
    int delete(String healthId);

    @Select("SELECT * FROM app_user WHERE name LIKE CONCAT('%', #{name}, '%')")
    List<User> findByNameContaining(String name);

    @Select("SELECT * FROM app_user WHERE email = #{email}")
    User findByEmail(String email);

    @Select("SELECT * FROM app_user WHERE verification_status = #{verificationStatus}")
    List<User> findByVerificationStatus(String verificationStatus);

    @Select("SELECT * FROM app_user WHERE family_id = #{familyId}")
    List<User> findByFamilyId(String familyId);
}