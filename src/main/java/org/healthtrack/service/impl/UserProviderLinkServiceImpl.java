package org.healthtrack.service.impl;

import org.healthtrack.entity.UserProviderLink;
import org.healthtrack.mapper.UserProviderLinkMapper;
import org.healthtrack.service.UserProviderLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * 用户-提供者关联服务实现类
 */
@Service
@Transactional
public class UserProviderLinkServiceImpl implements UserProviderLinkService {

    @Autowired
    private UserProviderLinkMapper userProviderLinkMapper;

    @Override
    public List<UserProviderLink> getAllLinks() {
        try {
            return userProviderLinkMapper.findAll();
        } catch (Exception e) {
            System.err.println("获取所有关联记录失败: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<UserProviderLink> getLinksByUserId(String healthId) {
        try {
            if (healthId == null || healthId.trim().isEmpty()) {
                throw new IllegalArgumentException("用户ID不能为空");
            }
            return userProviderLinkMapper.findByUserId(healthId);
        } catch (Exception e) {
            System.err.println("根据用户ID获取关联记录失败: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<UserProviderLink> getLinksByProviderId(String licenseNumber) {
        try {
            if (licenseNumber == null || licenseNumber.trim().isEmpty()) {
                throw new IllegalArgumentException("提供者许可证号不能为空");
            }
            return userProviderLinkMapper.findByProviderId(licenseNumber);
        } catch (Exception e) {
            System.err.println("根据提供者ID获取关联记录失败: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public UserProviderLink getPrimaryProvider(String healthId) {
        try {
            if (healthId == null || healthId.trim().isEmpty()) {
                throw new IllegalArgumentException("用户ID不能为空");
            }
            return userProviderLinkMapper.findPrimaryProvider(healthId);
        } catch (Exception e) {
            System.err.println("获取主要提供者失败: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean createLink(UserProviderLink link) {
        try {
            if (link == null || link.getHealthId() == null || link.getLicenseNumber() == null) {
                throw new IllegalArgumentException("关联信息不完整");
            }

            // 检查是否已存在关联
            if (existsLink(link.getHealthId(), link.getLicenseNumber())) {
                System.err.println("关联关系已存在");
                return false;
            }

            int result = userProviderLinkMapper.insert(link);
            return result > 0;
        } catch (Exception e) {
            System.err.println("创建关联记录失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateLink(UserProviderLink link) {
        try {
            if (link == null || link.getHealthId() == null || link.getLicenseNumber() == null) {
                throw new IllegalArgumentException("关联信息不完整");
            }

            // 检查关联是否存在
            if (!existsLink(link.getHealthId(), link.getLicenseNumber())) {
                System.err.println("关联关系不存在");
                return false;
            }

            int result = userProviderLinkMapper.update(link);
            return result > 0;
        } catch (Exception e) {
            System.err.println("更新关联记录失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteLink(String healthId, String licenseNumber) {
        try {
            if (healthId == null || licenseNumber == null) {
                throw new IllegalArgumentException("用户ID和提供者许可证号不能为空");
            }

            int result = userProviderLinkMapper.delete(healthId, licenseNumber);
            return result > 0;
        } catch (Exception e) {
            System.err.println("删除关联记录失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean existsLink(String healthId, String licenseNumber) {
        try {
            if (healthId == null || licenseNumber == null) {
                return false;
            }

            List<UserProviderLink> links = userProviderLinkMapper.findByUserId(healthId);
            return links.stream()
                    .anyMatch(link -> link.getLicenseNumber().equals(licenseNumber));
        } catch (Exception e) {
            System.err.println("检查关联关系失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public int countUsersByProvider(String licenseNumber) {
        try {
            if (licenseNumber == null || licenseNumber.trim().isEmpty()) {
                return 0;
            }
            List<UserProviderLink> links = userProviderLinkMapper.findByProviderId(licenseNumber);
            return links != null ? links.size() : 0;
        } catch (Exception e) {
            System.err.println("统计提供者用户数量失败: " + e.getMessage());
            return 0;
        }
    }

    @Override
    public int countProvidersByUser(String healthId) {
        try {
            if (healthId == null || healthId.trim().isEmpty()) {
                return 0;
            }
            List<UserProviderLink> links = userProviderLinkMapper.findByUserId(healthId);
            return links != null ? links.size() : 0;
        } catch (Exception e) {
            System.err.println("统计用户提供者数量失败: " + e.getMessage());
            return 0;
        }
    }
}