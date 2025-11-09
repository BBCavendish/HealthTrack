package org.healthtrack.entity;

import java.time.LocalDateTime;

public class Invitation {
    private String invitationId;
    private String inviteeContact;
    private LocalDateTime sentTime;
    private LocalDateTime expiredTime;
    private String status;
    private String invitationType;
    private String inviterId;
    private String relatedChallengeId;

    public Invitation() {}

    // getter/setter方法
    public String getInvitationId() { return invitationId; }
    public void setInvitationId(String invitationId) { this.invitationId = invitationId; }

    public String getInviteeContact() { return inviteeContact; }
    public void setInviteeContact(String inviteeContact) { this.inviteeContact = inviteeContact; }

    public LocalDateTime getSentTime() { return sentTime; }
    public void setSentTime(LocalDateTime sentTime) { this.sentTime = sentTime; }

    public LocalDateTime getExpiredTime() { return expiredTime; }
    public void setExpiredTime(LocalDateTime expiredTime) { this.expiredTime = expiredTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getInvitationType() { return invitationType; }
    public void setInvitationType(String invitationType) { this.invitationType = invitationType; }

    public String getInviterId() { return inviterId; }
    public void setInviterId(String inviterId) { this.inviterId = inviterId; }

    public String getRelatedChallengeId() { return relatedChallengeId; }
    public void setRelatedChallengeId(String relatedChallengeId) { this.relatedChallengeId = relatedChallengeId; }
}