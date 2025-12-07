package org.healthtrack.dto;

import org.healthtrack.entity.User;

/**
 * 活跃用户统计信息
 */
public class ActiveUserStats {
    private User user;
    private int healthRecordCount;      // 健康数据记录数
    private int completedChallengeCount; // 完成的挑战数
    
    public ActiveUserStats(User user, int healthRecordCount, int completedChallengeCount) {
        this.user = user;
        this.healthRecordCount = healthRecordCount;
        this.completedChallengeCount = completedChallengeCount;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public int getHealthRecordCount() {
        return healthRecordCount;
    }
    
    public void setHealthRecordCount(int healthRecordCount) {
        this.healthRecordCount = healthRecordCount;
    }
    
    public int getCompletedChallengeCount() {
        return completedChallengeCount;
    }
    
    public void setCompletedChallengeCount(int completedChallengeCount) {
        this.completedChallengeCount = completedChallengeCount;
    }
    
    public int getTotalActivityScore() {
        return healthRecordCount + completedChallengeCount;
    }
}

