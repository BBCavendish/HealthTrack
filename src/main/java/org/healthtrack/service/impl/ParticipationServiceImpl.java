package org.healthtrack.service.impl;

import org.healthtrack.entity.Participation;
import org.healthtrack.mapper.ParticipationMapper;
import org.healthtrack.service.ParticipationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 参与记录服务实现类
 */
@Service
@Transactional
public class ParticipationServiceImpl implements ParticipationService {

    @Autowired
    private ParticipationMapper participationMapper;

    @Override
    public List<Participation> getAllParticipations() {
        try {
            return participationMapper.findAll();
        } catch (Exception e) {
            System.err.println("获取所有参与记录失败: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<Participation> getParticipationsByUser(String healthId) {
        try {
            if (healthId == null || healthId.trim().isEmpty()) {
                throw new IllegalArgumentException("用户ID不能为空");
            }
            return participationMapper.findByUserId(healthId);
        } catch (Exception e) {
            System.err.println("获取用户参与记录失败: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<Participation> getParticipationsByChallenge(String challengeId) {
        try {
            if (challengeId == null || challengeId.trim().isEmpty()) {
                throw new IllegalArgumentException("挑战ID不能为空");
            }
            return participationMapper.findByChallengeId(challengeId);
        } catch (Exception e) {
            System.err.println("获取挑战参与记录失败: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public Participation getParticipation(String healthId, String challengeId) {
        try {
            if (healthId == null || challengeId == null) {
                throw new IllegalArgumentException("用户ID和挑战ID不能为空");
            }
            return participationMapper.findByUserAndChallenge(healthId, challengeId);
        } catch (Exception e) {
            System.err.println("获取特定参与记录失败: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean joinChallenge(Participation participation) {
        try {
            if (participation == null || participation.getHealthId() == null || participation.getChallengeId() == null) {
                throw new IllegalArgumentException("参与信息不完整");
            }

            // 检查是否已参与
            if (isUserParticipating(participation.getHealthId(), participation.getChallengeId())) {
                System.err.println("用户已参与该挑战");
                return false;
            }

            // 设置默认进度为0
            if (participation.getProgress() == null) {
                participation.setProgress(0);
            }

            int result = participationMapper.insert(participation);
            return result > 0;
        } catch (Exception e) {
            System.err.println("参与挑战失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean joinChallenge(String healthId, String challengeId) {
        try {
            if (healthId == null || challengeId == null) {
                throw new IllegalArgumentException("用户ID和挑战ID不能为空");
            }

            // 检查是否已参与
            if (isUserParticipating(healthId, challengeId)) {
                System.err.println("用户已参与该挑战");
                return false;
            }

            Participation participation = new Participation();
            participation.setHealthId(healthId);
            participation.setChallengeId(challengeId);
            participation.setProgress(0); // 默认进度为0

            int result = participationMapper.insert(participation);
            return result > 0;
        } catch (Exception e) {
            System.err.println("参与挑战失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateProgress(String healthId, String challengeId, Integer progress) {
        try {
            if (healthId == null || challengeId == null || progress == null) {
                throw new IllegalArgumentException("参数不能为空");
            }

            // 验证进度范围
            if (progress < 0 || progress > 100) {
                throw new IllegalArgumentException("进度必须在0-100之间");
            }

            Participation participation = participationMapper.findByUserAndChallenge(healthId, challengeId);
            if (participation == null) {
                System.err.println("参与记录不存在");
                return false;
            }

            participation.setProgress(progress);
            int result = participationMapper.update(participation);
            return result > 0;
        } catch (Exception e) {
            System.err.println("更新进度失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean leaveChallenge(String healthId, String challengeId) {
        try {
            if (healthId == null || challengeId == null) {
                throw new IllegalArgumentException("用户ID和挑战ID不能为空");
            }

            int result = participationMapper.delete(healthId, challengeId);
            return result > 0;
        } catch (Exception e) {
            System.err.println("退出挑战失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isUserParticipating(String healthId, String challengeId) {
        try {
            if (healthId == null || challengeId == null) {
                return false;
            }

            Participation participation = participationMapper.findByUserAndChallenge(healthId, challengeId);
            return participation != null;
        } catch (Exception e) {
            System.err.println("检查参与状态失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public int countChallengeParticipants(String challengeId) {
        try {
            if (challengeId == null || challengeId.trim().isEmpty()) {
                return 0;
            }
            List<Participation> participations = participationMapper.findByChallengeId(challengeId);
            return participations != null ? participations.size() : 0;
        } catch (Exception e) {
            System.err.println("统计参与人数失败: " + e.getMessage());
            return 0;
        }
    }

    @Override
    public int countUserParticipations(String healthId) {
        try {
            if (healthId == null || healthId.trim().isEmpty()) {
                return 0;
            }
            List<Participation> participations = participationMapper.findByUserId(healthId);
            return participations != null ? participations.size() : 0;
        } catch (Exception e) {
            System.err.println("统计用户参与数量失败: " + e.getMessage());
            return 0;
        }
    }

    @Override
    public List<Participation> getCompletedParticipations() {
        try {
            return participationMapper.findCompletedParticipations();
        } catch (Exception e) {
            System.err.println("获取已完成挑战记录失败: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public int getUserRankInChallenge(String healthId, String challengeId) {
        try {
            if (healthId == null || challengeId == null) {
                return -1;
            }

            List<Participation> participations = participationMapper.findByChallengeId(challengeId);
            if (participations.isEmpty()) {
                return -1;
            }

            // 按进度降序排序
            List<Participation> sorted = participations.stream()
                    .sorted(Comparator.comparingInt(Participation::getProgress).reversed())
                    .collect(Collectors.toList());

            // 查找用户排名
            for (int i = 0; i < sorted.size(); i++) {
                if (sorted.get(i).getHealthId().equals(healthId)) {
                    return i + 1; // 排名从1开始
                }
            }

            return -1; // 用户未参与该挑战
        } catch (Exception e) {
            System.err.println("计算用户排名失败: " + e.getMessage());
            return -1;
        }
    }

    @Override
    public int getChallengeParticipantsCount(String challengeId) {
        try {
            return participationMapper.countByChallengeId(challengeId);
        } catch (Exception e) {
            return 0;
        }
    }
}