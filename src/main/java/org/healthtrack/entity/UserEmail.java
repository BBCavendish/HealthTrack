package org.healthtrack.entity;

/**
 * 用户邮箱多值属性实体类
 */
public class UserEmail {
    private String healthId;        // 用户ID（复合主键部分）
    private String emailAddress;    // 邮箱地址（复合主键部分）
    private Boolean isPrimary;      // 是否为主邮箱

    // 构造方法
    public UserEmail() {}

    public UserEmail(String healthId, String emailAddress, Boolean isPrimary) {
        this.healthId = healthId;
        this.emailAddress = emailAddress;
        this.isPrimary = isPrimary;
    }

    // Getter和Setter方法
    public String getHealthId() { return healthId; }
    public void setHealthId(String healthId) { this.healthId = healthId; }

    public String getEmailAddress() { return emailAddress; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }

    public Boolean getIsPrimary() { return isPrimary; }
    public void setIsPrimary(Boolean isPrimary) { this.isPrimary = isPrimary; }

    @Override
    public String toString() {
        return String.format("UserEmail{healthId='%s', email='%s', isPrimary=%s}",
                healthId, emailAddress, isPrimary);
    }
}