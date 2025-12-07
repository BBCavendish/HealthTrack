package org.healthtrack.service.impl;

import org.healthtrack.entity.Provider;
import org.healthtrack.entity.ProviderEmail;
import org.healthtrack.mapper.ProviderMapper;
import org.healthtrack.mapper.ProviderEmailMapper;
import org.healthtrack.service.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class ProviderServiceImpl implements ProviderService {

    @Autowired
    private ProviderMapper providerMapper;

    @Autowired
    private ProviderEmailMapper providerEmailMapper;

    // ==================== 提供者管理方法 ====================

    @Override
    public List<Provider> getAllProviders() {
        try {
            return providerMapper.findAll();
        } catch (Exception e) {
            System.err.println("获取提供者列表失败: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public Provider getProviderById(String licenseNumber) {
        try {
            return providerMapper.findById(licenseNumber);
        } catch (Exception e) {
            System.err.println("获取提供者失败: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean saveProvider(Provider provider) {
        try {
            if (provider == null || provider.getLicenseNumber() == null) {
                throw new IllegalArgumentException("提供者信息不完整");
            }

            Provider existing = providerMapper.findById(provider.getLicenseNumber());
            if (existing != null) {
                return providerMapper.update(provider) > 0;
            } else {
                return providerMapper.insert(provider) > 0;
            }
        } catch (Exception e) {
            System.err.println("保存提供者失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteProvider(String licenseNumber) {
        try {
            // 先删除关联的邮箱记录
            providerEmailMapper.deleteByProviderId(licenseNumber);
            // 再删除提供者记录
            return providerMapper.delete(licenseNumber) > 0;
        } catch (Exception e) {
            System.err.println("删除提供者失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Provider> searchProvidersByName(String name) {
        try {
            return providerMapper.findByNameContaining(name);
        } catch (Exception e) {
            System.err.println("搜索提供者失败: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<Provider> getProvidersBySpecialty(String specialty) {
        try {
            return providerMapper.findBySpecialty(specialty);
        } catch (Exception e) {
            System.err.println("获取专业领域提供者失败: " +e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<Provider> getVerifiedProviders() {
        try {
            return providerMapper.findByVerifiedStatus("Verified");
        } catch (Exception e) {
            System.err.println("获取已认证提供者失败: " + e.getMessage());
            return List.of();
        }
    }

    // ==================== 邮箱管理方法 ====================

    @Override
    public List<ProviderEmail> getProviderEmails(String licenseNumber) {
        try {
            return providerEmailMapper.findByProviderId(licenseNumber);
        } catch (Exception e) {
            System.err.println("获取提供者邮箱失败: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public boolean addProviderEmail(String licenseNumber, String emailAddress, boolean isPrimary) {
        try {
            if (licenseNumber == null || emailAddress == null) {
                throw new IllegalArgumentException("参数不能为空");
            }

            // 如果设置为primary，先清除其他primary标记
            if (isPrimary) {
                clearProviderPrimaryFlags(licenseNumber);
            }

            ProviderEmail providerEmail = new ProviderEmail();
            providerEmail.setLicenseNumber(licenseNumber);
            providerEmail.setEmailAddress(emailAddress);
            providerEmail.setIsPrimary(isPrimary);

            return providerEmailMapper.insert(providerEmail) > 0;
        } catch (Exception e) {
            System.err.println("添加提供者邮箱失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean removeProviderEmail(String licenseNumber, String emailAddress) {
        try {
            return providerEmailMapper.delete(licenseNumber, emailAddress) > 0;
        } catch (Exception e) {
            System.err.println("删除提供者邮箱失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean setPrimaryProviderEmail(String licenseNumber, String emailAddress) {
        try {
            // 先清除所有primary标记
            clearProviderPrimaryFlags(licenseNumber);

            // 设置新的primary邮箱
            ProviderEmail providerEmail = new ProviderEmail();
            providerEmail.setLicenseNumber(licenseNumber);
            providerEmail.setEmailAddress(emailAddress);
            providerEmail.setIsPrimary(true);

            return providerEmailMapper.update(providerEmail) > 0;
        } catch (Exception e) {
            System.err.println("设置主邮箱失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public ProviderEmail getPrimaryProviderEmail(String licenseNumber) {
        try {
            return providerEmailMapper.findPrimaryEmail(licenseNumber);
        } catch (Exception e) {
            System.err.println("获取主邮箱失败: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Provider getProviderByEmail(String email) {
        try {
            if (email == null || email.trim().isEmpty()) {
                return null;
            }

            // 首先在provider_email表中查找匹配的邮箱
            List<ProviderEmail> providerEmails = providerEmailMapper.findByEmailAddress(email);
            if (providerEmails.isEmpty()) {
                return null;
            }

            // 获取第一个匹配的提供者执照号
            String licenseNumber = providerEmails.get(0).getLicenseNumber();
            if (licenseNumber == null) {
                return null;
            }

            // 根据执照号获取完整的提供者信息
            return providerMapper.findById(licenseNumber);
        } catch (Exception e) {
            System.err.println("根据邮箱查找提供者失败: " + e.getMessage());
            return null;
        }
    }

    // 私有辅助方法
    private void clearProviderPrimaryFlags(String licenseNumber) {
        try {
            // 实现清除primary标记的逻辑
            List<ProviderEmail> emails = providerEmailMapper.findByProviderId(licenseNumber);
            for (ProviderEmail email : emails) {
                if (email.getIsPrimary()) {
                    email.setIsPrimary(false);
                    providerEmailMapper.update(email);
                }
            }
        } catch (Exception e) {
            System.err.println("清除primary标记失败: " + e.getMessage());
        }
    }
}