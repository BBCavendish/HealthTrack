package org.healthtrack.service;

import org.healthtrack.dto.ChallengeWithParticipants;
import org.healthtrack.entity.WellnessChallenge;
import java.util.List;

public interface WellnessChallengeService {
    List<WellnessChallenge> getAllChallenges();
    WellnessChallenge getChallengeById(String challengeId);
    boolean saveChallenge(WellnessChallenge challenge);
    boolean deleteChallenge(String challengeId);
    List<WellnessChallenge> getChallengesByCreator(String creatorId);
    List<WellnessChallenge> getActiveChallenges();
    int countChallengesByUser(String userId);
    
    /**
     * 获取参与人数最多的挑战
     * @param limit 返回数量限制
     * @return 挑战列表（按参与人数降序排列）
     */
    List<ChallengeWithParticipants> getMostPopularChallenges(int limit);
}