package org.healthtrack.service.impl;

import org.healthtrack.entity.User;
import org.healthtrack.entity.UserEmail;
import org.healthtrack.mapper.UserMapper;
import org.healthtrack.mapper.UserEmailMapper;
import org.healthtrack.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserEmailMapper userEmailMapper;

    // ==================== 用户管理方法 ====================

    @Override
    public List<User> getAllUsers() {
        try {
            return userMapper.findAll();
        } catch (Exception e) {
            System.err.println("获取用户列表失败: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public User getUserById(String healthId) {
        try {
            return userMapper.findById(healthId);
        } catch (Exception e) {
            System.err.println("获取用户失败: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean saveUser(User user) {
        try {
            if (user == null || user.getHealthId() == null) {
                throw new IllegalArgumentException("用户信息不完整");
            }

            User existing = userMapper.findById(user.getHealthId());
            if (existing != null) {
                return userMapper.update(user) > 0;
            } else {
                return userMapper.insert(user) > 0;
            }
        } catch (Exception e) {
            System.err.println("保存用户失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteUser(String healthId) {
        try {
            // 先删除关联的邮箱记录
            userEmailMapper.deleteByUserId(healthId);
            // 再删除用户记录
            return userMapper.delete(healthId) > 0;
        } catch (Exception e) {
            System.err.println("删除用户失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<User> searchUsersByName(String name) {
        try {
            return userMapper.findByNameContaining(name);
        } catch (Exception e) {
            System.err.println("搜索用户失败: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<User> getUsersByFamily(String familyId) {
        try {
            return userMapper.findByFamilyId(familyId);
        } catch (Exception e) {
            System.err.println("获取家庭成员失败: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<User> getUsersByVerificationStatus(String status) {
        try {
            return userMapper.findByVerificationStatus(status);
        } catch (Exception e) {
            System.err.println("获取验证状态用户失败: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public int getTotalUserCount() {
        try {
            List<User> users = userMapper.findAll();
            return users != null ? users.size() : 0;
        } catch (Exception e) {
            System.err.println("统计用户总数失败: " + e.getMessage());
            return 0;
        }
    }

    @Override
    public int getVerifiedUserCount() {
        try {
            List<User> users = userMapper.findByVerificationStatus("Verified");
            return users != null ? users.size() : 0;
        } catch (Exception e) {
            System.err.println("统计已认证用户失败: " + e.getMessage());
            return 0;
        }
    }

    @Override
    public List<User> getUsersWithMostHealthRecords(int limit) {
        try {
            // 这里需要实现获取健康记录最多的用户逻辑
            // 暂时返回所有用户，实际应该根据健康报告数量排序
            return userMapper.findAll().stream()
                    .limit(limit)
                    .toList();
        } catch (Exception e) {
            System.err.println("获取最活跃用户失败: " + e.getMessage());
            return List.of();
        }
    }

    // ==================== 邮箱管理方法 ====================

    @Override
    public List<UserEmail> getUserEmails(String healthId) {
        try {
            return userEmailMapper.findByUserId(healthId);
        } catch (Exception e) {
            System.err.println("获取用户邮箱失败: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public boolean addUserEmail(String healthId, String emailAddress, boolean isPrimary) {
        try {
            if (healthId == null || emailAddress == null) {
                throw new IllegalArgumentException("参数不能为空");
            }

            // 如果设置为primary，先清除其他primary标记
            if (isPrimary) {
                userEmailMapper.clearPrimaryFlags(healthId);
            }

            UserEmail userEmail = new UserEmail();
            userEmail.setHealthId(healthId);
            userEmail.setEmailAddress(emailAddress);
            userEmail.setIsPrimary(isPrimary);

            return userEmailMapper.insert(userEmail) > 0;
        } catch (Exception e) {
            System.err.println("添加用户邮箱失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean removeUserEmail(String healthId, String emailAddress) {
        try {
            return userEmailMapper.delete(healthId, emailAddress) > 0;
        } catch (Exception e) {
            System.err.println("删除用户邮箱失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean setPrimaryEmail(String healthId, String emailAddress) {
        try {
            // 先清除所有primary标记
            userEmailMapper.clearPrimaryFlags(healthId);

            // 设置新的primary邮箱
            UserEmail userEmail = new UserEmail();
            userEmail.setHealthId(healthId);
            userEmail.setEmailAddress(emailAddress);
            userEmail.setIsPrimary(true);

            return userEmailMapper.update(userEmail) > 0;
        } catch (Exception e) {
            System.err.println("设置主邮箱失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public UserEmail getPrimaryEmail(String healthId) {
        try {
            return userEmailMapper.findPrimaryEmail(healthId);
        } catch (Exception e) {
            System.err.println("获取主邮箱失败: " + e.getMessage());
            return null;
        }
    }

    @Override
    public int getUserEmailCount(String healthId) {
        try {
            List<UserEmail> emails = userEmailMapper.findByUserId(healthId);
            return emails != null ? emails.size() : 0;
        } catch (Exception e) {
            System.err.println("统计用户邮箱数量失败: " + e.getMessage());
            return 0;
        }
    }

    @Override
    public boolean existsUser(String healthId) {
        try {
            if (healthId == null || healthId.trim().isEmpty()) {
                return false;
            }

            User user = userMapper.findById(healthId);
            return user != null;
        } catch (Exception e) {
            System.err.println("检查用户存在性失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public User getUserByEmail(String email) {
        try {
            if (email == null || email.trim().isEmpty()) {
                return null;
            }

            // 首先在user_email表中查找匹配的邮箱
            List<UserEmail> userEmails = userEmailMapper.findByEmailAddress(email);
            if (userEmails.isEmpty()) {
                return null;
            }

            // 获取第一个匹配的用户ID
            String healthId = userEmails.get(0).getHealthId();
            if (healthId == null) {
                return null;
            }

            // 根据用户ID获取完整的用户信息
            return userMapper.findById(healthId);
        } catch (Exception e) {
            System.err.println("根据邮箱查找用户失败: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<User> getUsersByFamilyId(String familyId) {
        try {
            if (familyId == null || familyId.trim().isEmpty()) {
                throw new IllegalArgumentException("家庭ID不能为空");
            }

            return userMapper.findByFamilyId(familyId);
        } catch (Exception e) {
            System.err.println("根据家庭ID获取用户失败: " + e.getMessage());
            return List.of();
        }
    }
}