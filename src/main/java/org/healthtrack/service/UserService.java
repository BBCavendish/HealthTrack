package org.healthtrack.service;

import org.healthtrack.entity.User;
import org.healthtrack.entity.UserEmail;
import java.util.List;

public interface UserService {

    // ==================== 用户管理方法 ====================
    List<User> getAllUsers();
    User getUserById(String healthId);
    boolean saveUser(User user);
    boolean deleteUser(String healthId);
    List<User> searchUsersByName(String name);
    List<User> getUsersByFamily(String familyId);
    List<User> getUsersByVerificationStatus(String status);

    // ==================== 图片中需要补充的三个方法 ====================
    /**
     * 检查用户是否存在
     * @param healthId 用户健康ID
     * @return 存在返回true，否则返回false
     */
    boolean existsUser(String healthId);

    /**
     * 根据邮箱地址查找用户
     * @param email 邮箱地址
     * @return 用户对象，如果不存在返回null
     */
    User getUserByEmail(String email);

    /**
     * 根据家庭ID获取家庭成员列表
     * @param familyId 家庭ID
     * @return 家庭成员列表
     */
    List<User> getUsersByFamilyId(String familyId);

    // ==================== 统计方法 ====================
    int getTotalUserCount();
    int getVerifiedUserCount();
    List<User> getUsersWithMostHealthRecords(int limit);

    // ==================== 邮箱管理方法 ====================
    List<UserEmail> getUserEmails(String healthId);
    boolean addUserEmail(String healthId, String emailAddress, boolean isPrimary);
    boolean removeUserEmail(String healthId, String emailAddress);
    boolean setPrimaryEmail(String healthId, String emailAddress);
    UserEmail getPrimaryEmail(String healthId);
    int getUserEmailCount(String healthId);
    
    // ==================== 活跃用户统计方法 ====================
    /**
     * 获取最活跃的用户（按健康数据记录数或完成挑战数）
     * @param limit 返回数量限制
     * @param sortBy 排序方式：health_records（健康记录数）、challenges（完成挑战数）、total（综合）
     * @return 活跃用户统计列表
     */
    List<org.healthtrack.dto.ActiveUserStats> getMostActiveUsers(int limit, String sortBy);
}