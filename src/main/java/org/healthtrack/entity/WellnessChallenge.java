package org.healthtrack.entity;

import java.time.LocalDate;

public class WellnessChallenge {
    private String challengeId;
    private String goal;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private String creatorId;

    public WellnessChallenge() {}

    public WellnessChallenge(String challengeId, LocalDate startDate, LocalDate endDate, String creatorId) {
        this.challengeId = challengeId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.creatorId = creatorId;
    }

    // getter/setter方法
    public String getChallengeId() { return challengeId; }
    public void setChallengeId(String challengeId) { this.challengeId = challengeId; }

    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCreatorId() { return creatorId; }
    public void setCreatorId(String creatorId) { this.creatorId = creatorId; }
}