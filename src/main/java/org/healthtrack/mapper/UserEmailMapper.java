package org.healthtrack.mapper;

import org.apache.ibatis.annotations.*;
import org.healthtrack.entity.UserEmail;
import java.util.List;

@Mapper
public interface UserEmailMapper {

    // 现有方法...
    @Select("SELECT * FROM user_email WHERE health_id = #{healthId}")
    List<UserEmail> findByUserId(String healthId);

    @Select("SELECT * FROM user_email WHERE health_id = #{healthId} AND is_primary = TRUE")
    UserEmail findPrimaryEmail(String healthId);

    @Insert("INSERT INTO user_email (health_id, email_address, is_primary) VALUES (#{healthId}, #{emailAddress}, #{isPrimary})")
    int insert(UserEmail userEmail);

    @Update("UPDATE user_email SET is_primary = #{isPrimary} WHERE health_id = #{healthId} AND email_address = #{emailAddress}")
    int update(UserEmail userEmail);

    @Delete("DELETE FROM user_email WHERE health_id = #{healthId} AND email_address = #{emailAddress}")
    int delete(@Param("healthId") String healthId, @Param("emailAddress") String emailAddress);

    @Delete("DELETE FROM user_email WHERE health_id = #{healthId}")
    int deleteByUserId(String healthId);

    @Update("UPDATE user_email SET is_primary = FALSE WHERE health_id = #{healthId}")
    int clearPrimaryFlags(String healthId);

    // ==================== 新增方法：根据邮箱地址查找 ====================
    /**
     * 根据邮箱地址查找用户邮箱记录
     * @param emailAddress 邮箱地址
     * @return 匹配的用户邮箱列表
     */
    @Select("SELECT * FROM user_email WHERE email_address = #{emailAddress}")
    List<UserEmail> findByEmailAddress(String emailAddress);

    /**
     * 检查邮箱地址是否已存在
     * @param emailAddress 邮箱地址
     * @return 存在返回true，否则返回false
     */
    @Select("SELECT COUNT(*) FROM user_email WHERE email_address = #{emailAddress}")
    boolean existsByEmailAddress(String emailAddress);
}