package org.healthtrack.service;

import org.healthtrack.entity.Participation;
import java.util.List;

/**
 * 参与记录服务接口
 * 管理用户参与健康挑战的相关业务逻辑
 */
public interface ParticipationService {

    /**
     * 获取所有参与记录
     */
    List<Participation> getAllParticipations();

    /**
     * 根据用户ID获取参与记录
     */
    List<Participation> getParticipationsByUser(String healthId);

    /**
     * 根据挑战ID获取参与记录
     */
    List<Participation> getParticipationsByChallenge(String challengeId);

    /**
     * 获取特定用户参与特定挑战的记录
     */
    Participation getParticipation(String healthId, String challengeId);

    /**
     * 用户参与挑战
     */
    boolean joinChallenge(Participation participation);

    /**
     * 用户参与挑战（简化版）
     */
    boolean joinChallenge(String healthId, String challengeId);

    /**
     * 更新参与进度
     */
    boolean updateProgress(String healthId, String challengeId, Integer progress);

    /**
     * 用户退出挑战
     */
    boolean leaveChallenge(String healthId, String challengeId);

    /**
     * 检查用户是否已参与某个挑战
     */
    boolean isUserParticipating(String healthId, String challengeId);

    /**
     * 统计挑战的参与人数
     */
    int countChallengeParticipants(String challengeId);

    /**
     * 统计用户参与的挑战数量
     */
    int countUserParticipations(String healthId);

    /**
     * 获取已完成挑战的用户列表
     */
    List<Participation> getCompletedParticipations();

    /**
     * 获取用户在某挑战中的排名（按进度）
     */
    int getUserRankInChallenge(String healthId, String challengeId);

    int getChallengeParticipantsCount(String challengeId);
}