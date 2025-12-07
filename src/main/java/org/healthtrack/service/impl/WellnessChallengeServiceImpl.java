package org.healthtrack.service.impl;

import org.healthtrack.dto.ChallengeWithParticipants;
import org.healthtrack.entity.WellnessChallenge;
import org.healthtrack.mapper.WellnessChallengeMapper;
import org.healthtrack.service.ParticipationService;
import org.healthtrack.service.WellnessChallengeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class WellnessChallengeServiceImpl implements WellnessChallengeService {

    @Autowired
    private WellnessChallengeMapper challengeMapper;
    
    @Autowired
    private ParticipationService participationService;

    @Override
    public List<WellnessChallenge> getAllChallenges() {
        try {
            return challengeMapper.findAll();
        } catch (Exception e) {
            System.err.println("获取挑战列表失败: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public WellnessChallenge getChallengeById(String challengeId) {
        try {
            return challengeMapper.findById(challengeId);
        } catch (Exception e) {
            System.err.println("根据ID查询挑战失败: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean saveChallenge(WellnessChallenge challenge) {
        try {
            if (challenge == null || challenge.getChallengeId() == null) {
                System.err.println("挑战信息不完整");
                return false;
            }

            WellnessChallenge existing = challengeMapper.findById(challenge.getChallengeId());
            if (existing != null) {
                int result = challengeMapper.update(challenge);
                return result > 0;
            } else {
                int result = challengeMapper.insert(challenge);
                return result > 0;
            }
        } catch (Exception e) {
            System.err.println("保存挑战失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteChallenge(String challengeId) {
        try {
            if (challengeId == null || challengeId.trim().isEmpty()) {
                System.err.println("挑战ID不能为空");
                return false;
            }

            int result = challengeMapper.delete(challengeId);
            return result > 0;
        } catch (Exception e) {
            System.err.println("删除挑战失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<WellnessChallenge> getChallengesByCreator(String creatorId) {
        try {
            if (creatorId == null || creatorId.trim().isEmpty()) {
                System.err.println("创建者ID不能为空");
                return List.of();
            }
            return challengeMapper.findByCreatorId(creatorId);
        } catch (Exception e) {
            System.err.println("根据创建者查询挑战失败: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<WellnessChallenge> getActiveChallenges() {
        try {
            LocalDate today = LocalDate.now();
            List<WellnessChallenge> allChallenges = challengeMapper.findAll();

            return allChallenges.stream()
                    .filter(challenge -> challenge.getStartDate() != null && challenge.getEndDate() != null)
                    .filter(challenge -> !today.isBefore(challenge.getStartDate()) && !today.isAfter(challenge.getEndDate()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("获取进行中挑战失败: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public int countChallengesByUser(String userId) {
        try {
            if (userId == null || userId.trim().isEmpty()) {
                return 0;
            }
            List<WellnessChallenge> challenges = challengeMapper.findByCreatorId(userId);
            return challenges != null ? challenges.size() : 0;
        } catch (Exception e) {
            System.err.println("统计用户挑战数量失败: " + e.getMessage());
            return 0;
        }
    }
    
    @Override
    public List<ChallengeWithParticipants> getMostPopularChallenges(int limit) {
        try {
            List<WellnessChallenge> allChallenges = challengeMapper.findAll();
            
            return allChallenges.stream()
                .map(challenge -> {
                    int participantCount = participationService != null ? 
                        participationService.getChallengeParticipantsCount(challenge.getChallengeId()) : 0;
                    return new ChallengeWithParticipants(challenge, participantCount);
                })
                .sorted(Comparator.comparingInt(ChallengeWithParticipants::getParticipantCount).reversed())
                .limit(limit > 0 ? limit : Integer.MAX_VALUE)
                .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("获取最受欢迎挑战失败: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }
}