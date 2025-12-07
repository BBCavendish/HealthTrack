package org.healthtrack.dto;

import org.healthtrack.entity.WellnessChallenge;

/**
 * 带参与人数的挑战信息
 */
public class ChallengeWithParticipants {
    private WellnessChallenge challenge;
    private int participantCount;
    
    public ChallengeWithParticipants(WellnessChallenge challenge, int participantCount) {
        this.challenge = challenge;
        this.participantCount = participantCount;
    }
    
    public WellnessChallenge getChallenge() {
        return challenge;
    }
    
    public void setChallenge(WellnessChallenge challenge) {
        this.challenge = challenge;
    }
    
    public int getParticipantCount() {
        return participantCount;
    }
    
    public void setParticipantCount(int participantCount) {
        this.participantCount = participantCount;
    }
}

