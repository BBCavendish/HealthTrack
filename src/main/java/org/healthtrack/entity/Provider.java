package org.healthtrack.entity;

/**
 * 医疗提供者实体类 - 修正版（移除email字段）
 */
public class Provider {
    private String licenseNumber;      // 许可证号（主键）
    private String name;               // 姓名
    private String specialty;          // 专业领域
    private String verifiedStatus;     // 验证状态：Verified/Unverified
    private String phone;              // 电话

    // 构造方法
    public Provider() {}

    public Provider(String licenseNumber, String name, String specialty, String verifiedStatus, String phone) {
        this.licenseNumber = licenseNumber;
        this.name = name;
        this.specialty = specialty;
        this.verifiedStatus = verifiedStatus;
        this.phone = phone;
    }

    // Getter和Setter方法
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public String getVerifiedStatus() { return verifiedStatus; }
    public void setVerifiedStatus(String verifiedStatus) { this.verifiedStatus = verifiedStatus; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public String toString() {
        return String.format("Provider{licenseNumber='%s', name='%s', specialty='%s', status='%s', phone='%s'}",
                licenseNumber, name, specialty, verifiedStatus, phone);
    }
}