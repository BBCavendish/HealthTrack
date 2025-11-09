package org.healthtrack.service;

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
}