package org.healthtrack.service;

import org.healthtrack.entity.Provider;
import org.healthtrack.entity.ProviderEmail;
import java.util.List;

public interface ProviderService {

    // 原有的提供者管理方法
    List<Provider> getAllProviders();
    Provider getProviderById(String licenseNumber);
    boolean saveProvider(Provider provider);
    boolean deleteProvider(String licenseNumber);
    List<Provider> searchProvidersByName(String name);
    List<Provider> getProvidersBySpecialty(String specialty);
    List<Provider> getVerifiedProviders();

    // 新增的邮箱管理方法（与UserService对称）
    List<ProviderEmail> getProviderEmails(String licenseNumber);
    boolean addProviderEmail(String licenseNumber, String emailAddress, boolean isPrimary);
    boolean removeProviderEmail(String licenseNumber, String emailAddress);
    boolean setPrimaryProviderEmail(String licenseNumber, String emailAddress);
    ProviderEmail getPrimaryProviderEmail(String licenseNumber);
}