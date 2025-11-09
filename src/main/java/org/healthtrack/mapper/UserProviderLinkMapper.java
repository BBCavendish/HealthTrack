package org.healthtrack.mapper;

import org.apache.ibatis.annotations.*;
import org.healthtrack.entity.UserProviderLink;
import java.util.List;

@Mapper
public interface UserProviderLinkMapper {

    /**
     * 查询所有用户-提供者关联记录
     */
    @Select("SELECT * FROM user_provider_link")
    List<UserProviderLink> findAll();

    /**
     * 根据用户ID查询关联的提供者
     */
    @Select("SELECT * FROM user_provider_link WHERE health_id = #{healthId}")
    List<UserProviderLink> findByUserId(String healthId);

    /**
     * 根据提供者ID查询关联的用户
     */
    @Select("SELECT * FROM user_provider_link WHERE license_number = #{licenseNumber}")
    List<UserProviderLink> findByProviderId(String licenseNumber);

    /**
     * 查询用户的主要提供者
     */
    @Select("SELECT * FROM user_provider_link WHERE health_id = #{healthId} AND is_primary = true")
    UserProviderLink findPrimaryProvider(String healthId);

    /**
     * 插入新的关联记录
     */
    @Insert("INSERT INTO user_provider_link (health_id, license_number, is_primary) " +
            "VALUES (#{healthId}, #{licenseNumber}, #{isPrimary})")
    int insert(UserProviderLink link);

    /**
     * 更新关联记录（主要用于设置主要提供者）
     */
    @Update("UPDATE user_provider_link SET is_primary = #{isPrimary} " +
            "WHERE health_id = #{healthId} AND license_number = #{licenseNumber}")
    int update(UserProviderLink link);

    /**
     * 删除关联记录
     */
    @Delete("DELETE FROM user_provider_link WHERE health_id = #{healthId} AND license_number = #{licenseNumber}")
    int delete(@Param("healthId") String healthId, @Param("licenseNumber") String licenseNumber);

    /**
     * 检查关联是否存在
     */
    @Select("SELECT COUNT(*) FROM user_provider_link " +
            "WHERE health_id = #{healthId} AND license_number = #{licenseNumber}")
    int exists(@Param("healthId") String healthId, @Param("licenseNumber") String licenseNumber);
}