package org.healthtrack.entity;

/**
 * 用户实体类 - 修正版（移除email字段）
 */
public class User {
    private String healthId;           // 健康ID（主键）
    private String name;               // 姓名
    private String phone;              // 电话
    private String verificationStatus;  // 验证状态：Verified/Unverified
    private String role;               // 角色：普通用户/管理员
    private String familyId;           // 家庭组ID（外键）

    // 构造方法
    public User() {}

    public User(String healthId, String name, String phone, String verificationStatus, String role) {
        this.healthId = healthId;
        this.name = name;
        this.phone = phone;
        this.verificationStatus = verificationStatus;
        this.role = role;
    }

    // Getter和Setter方法
    public String getHealthId() { return healthId; }
    public void setHealthId(String healthId) { this.healthId = healthId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getFamilyId() { return familyId; }
    public void setFamilyId(String familyId) { this.familyId = familyId; }

    @Override
    public String toString() {
        return String.format("User{healthId='%s', name='%s', phone='%s', status='%s', role='%s', familyId='%s'}",
                healthId, name, phone, verificationStatus, role, familyId);
    }
}