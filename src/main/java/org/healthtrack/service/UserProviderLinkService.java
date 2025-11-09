package org.healthtrack.service;

import org.healthtrack.entity.UserProviderLink;
import java.util.List;

/**
 * 用户-提供者关联服务接口
 * 管理用户与医疗提供者之间的关联关系
 */
public interface UserProviderLinkService {

    /**
     * 获取所有用户-提供者关联记录
     */
    List<UserProviderLink> getAllLinks();

    /**
     * 根据用户ID获取关联的提供者
     */
    List<UserProviderLink> getLinksByUserId(String healthId);

    /**
     * 根据提供者ID获取关联的用户
     */
    List<UserProviderLink> getLinksByProviderId(String licenseNumber);

    /**
     * 获取用户的主要提供者
     */
    UserProviderLink getPrimaryProvider(String healthId);

    /**
     * 创建用户-提供者关联
     */
    boolean createLink(UserProviderLink link);

    /**
     * 更新关联关系（如设置主要提供者）
     */
    boolean updateLink(UserProviderLink link);

    /**
     * 删除用户-提供者关联
     */
    boolean deleteLink(String healthId, String licenseNumber);

    /**
     * 检查用户是否已关联某个提供者
     */
    boolean existsLink(String healthId, String licenseNumber);

    /**
     * 统计提供者的用户数量
     */
    int countUsersByProvider(String licenseNumber);

    /**
     * 统计用户的提供者数量
     */
    int countProvidersByUser(String healthId);
}