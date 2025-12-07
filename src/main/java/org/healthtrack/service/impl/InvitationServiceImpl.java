package org.healthtrack.service.impl;

import org.healthtrack.entity.Invitation;
import org.healthtrack.mapper.InvitationMapper;
import org.healthtrack.service.InvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 邀请服务实现类
 * 处理邀请相关的业务逻辑
 */
@Service
@Transactional
public class InvitationServiceImpl implements InvitationService {

    @Autowired
    private InvitationMapper invitationMapper;

    /**
     * 获取所有邀请
     * @return 邀请列表
     */
    @Override
    public List<Invitation> getAllInvitations() {
        try {
            return invitationMapper.findAll();
        } catch (Exception e) {
            System.err.println("获取邀请列表失败: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * 根据ID获取邀请
     * @param invitationId 邀请ID
     * @return 邀请对象，如果不存在返回null
     */
    @Override
    public Invitation getInvitationById(String invitationId) {
        try {
            return invitationMapper.findById(invitationId);
        } catch (Exception e) {
            System.err.println("根据ID获取邀请失败: " + e.getMessage());
            return null;
        }
    }

    /**
     * 保存邀请（新增或更新）
     * @param invitation 邀请对象
     * @return 保存成功返回true，失败返回false
     */
    @Override
    public boolean saveInvitation(Invitation invitation) {
        try {
            Invitation existing = invitationMapper.findById(invitation.getInvitationId());
            if (existing != null) {
                return invitationMapper.update(invitation) > 0;
            } else {
                return invitationMapper.insert(invitation) > 0;
            }
        } catch (Exception e) {
            System.err.println("保存邀请失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 删除邀请
     * @param invitationId 邀请ID
     * @return 删除成功返回true，失败返回false
     */
    @Override
    public boolean deleteInvitation(String invitationId) {
        try {
            return invitationMapper.delete(invitationId) > 0;
        } catch (Exception e) {
            System.err.println("删除邀请失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 根据邀请人ID获取邀请
     * @param inviterId 邀请人ID
     * @return 该邀请人发出的邀请列表
     */
    @Override
    public List<Invitation> getInvitationsByInviter(String inviterId) {
        try {
            return invitationMapper.findByInviterId(inviterId);
        } catch (Exception e) {
            System.err.println("获取邀请人邀请列表失败: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * 根据状态获取邀请
     * @param status 邀请状态
     * @return 指定状态的邀请列表
     */
    @Override
    public List<Invitation> getInvitationsByStatus(String status) {
        try {
            return invitationMapper.findByStatus(status);
        } catch (Exception e) {
            System.err.println("获取状态邀请列表失败: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * 过期处理：将过期的待处理邀请标记为过期
     * @return 处理成功的邀请数量
     */
    @Override
    public boolean expireOldInvitations() {
        try {
            List<Invitation> expiredInvitations = invitationMapper.findExpiredInvitations();
            if (expiredInvitations.isEmpty()) {
                return true;
            }

            for (Invitation invitation : expiredInvitations) {
                invitation.setStatus("Expired");
                invitationMapper.update(invitation);
            }
            return true;
        } catch (Exception e) {
            System.err.println("处理过期邀请失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 检查邀请是否有效（未过期且状态为Pending）
     * @param invitationId 邀请ID
     * @return 有效返回true，否则返回false
     */
    public boolean isInvitationValid(String invitationId) {
        try {
            Invitation invitation = invitationMapper.findById(invitationId);
            if (invitation == null) {
                return false;
            }

            return "Pending".equals(invitation.getStatus()) &&
                    invitation.getExpiredTime().isAfter(LocalDateTime.now());
        } catch (Exception e) {
            System.err.println("检查邀请有效性失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Invitation> getInvitationsByInvitee(String inviteeContact) {
        try {
            return invitationMapper.findByInviteeContact(inviteeContact);
        } catch (Exception e) {
            System.err.println("获取被邀请人邀请列表失败: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * 接受邀请
     * @param invitationId 邀请ID
     * @return 接受成功返回true，失败返回false
     */
    @Override
    public boolean acceptInvitation(String invitationId) {
        try {
            Invitation invitation = invitationMapper.findById(invitationId);
            if (invitation == null || !isInvitationValid(invitationId)) {
                return false;
            }

            invitation.setStatus("Accepted");
            return invitationMapper.update(invitation) > 0;
        } catch (Exception e) {
            System.err.println("接受邀请失败: " + e.getMessage());
            return false;
        }
    }
}