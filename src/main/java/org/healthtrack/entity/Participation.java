package org.healthtrack.entity;

/**
 * 参与记录实体类
 * 表示用户参与健康挑战的多对多关系，包含进度信息
 */
public class Participation {
    private String healthId;        // 用户ID
    private String challengeId;     // 挑战ID
    private Integer progress;       // 进度（0-100）

    public Participation() {}

    public Participation(String healthId, String challengeId, Integer progress) {
        this.healthId = healthId;
        this.challengeId = challengeId;
        this.progress = progress;
    }

    // getter/setter方法
    public String getHealthId() { return healthId; }
    public void setHealthId(String healthId) { this.healthId = healthId; }

    public String getChallengeId() { return challengeId; }
    public void setChallengeId(String challengeId) { this.challengeId = challengeId; }

    public Integer getProgress() { return progress; }
    public void setProgress(Integer progress) {
        // 进度验证：0-100之间
        if (progress != null && (progress < 0 || progress > 100)) {
            throw new IllegalArgumentException("进度必须在0-100之间");
        }
        this.progress = progress;
    }

    @Override
    public String toString() {
        return String.format("Participation{healthId='%s', challengeId='%s', progress=%d}",
                healthId, challengeId, progress);
    }
}