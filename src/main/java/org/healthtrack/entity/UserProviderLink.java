package org.healthtrack.entity;

/**
 * 用户-提供者关联实体类
 * 表示用户与医疗提供者之间的多对多关系
 */
public class UserProviderLink {
    private String healthId;        // 用户ID
    private String licenseNumber;   // 提供者许可证号
    private Boolean isPrimary;      // 是否为主要提供者

    public UserProviderLink() {}

    public UserProviderLink(String healthId, String licenseNumber, Boolean isPrimary) {
        this.healthId = healthId;
        this.licenseNumber = licenseNumber;
        this.isPrimary = isPrimary;
    }

    // getter/setter方法
    public String getHealthId() { return healthId; }
    public void setHealthId(String healthId) { this.healthId = healthId; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public Boolean getIsPrimary() { return isPrimary; }
    public void setIsPrimary(Boolean isPrimary) { this.isPrimary = isPrimary; }

    @Override
    public String toString() {
        return String.format("UserProviderLink{healthId='%s', licenseNumber='%s', isPrimary=%s}",
                healthId, licenseNumber, isPrimary);
    }
}