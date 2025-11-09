package org.healthtrack.entity;

/**
 * 提供者邮箱多值属性实体类
 */
public class ProviderEmail {
    private String licenseNumber;   // 许可证号（复合主键部分）
    private String emailAddress;    // 邮箱地址（复合主键部分）
    private Boolean isPrimary;      // 是否为主邮箱

    // 构造方法
    public ProviderEmail() {}

    public ProviderEmail(String licenseNumber, String emailAddress, Boolean isPrimary) {
        this.licenseNumber = licenseNumber;
        this.emailAddress = emailAddress;
        this.isPrimary = isPrimary;
    }

    // Getter和Setter方法
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public String getEmailAddress() { return emailAddress; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }

    public Boolean getIsPrimary() { return isPrimary; }
    public void setIsPrimary(Boolean isPrimary) { this.isPrimary = isPrimary; }

    @Override
    public String toString() {
        return String.format("ProviderEmail{licenseNumber='%s', email='%s', isPrimary=%s}",
                licenseNumber, emailAddress, isPrimary);
    }
}