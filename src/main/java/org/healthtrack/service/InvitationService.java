package org.healthtrack.service;

import org.healthtrack.entity.Invitation;
import java.util.List;

public interface InvitationService {
    List<Invitation> getAllInvitations();
    Invitation getInvitationById(String invitationId);
    boolean saveInvitation(Invitation invitation);
    boolean deleteInvitation(String invitationId);
    List<Invitation> getInvitationsByInviter(String inviterId);
    List<Invitation> getInvitationsByStatus(String status);
    List<Invitation> getInvitationsByInvitee(String inviteeContact);
    boolean expireOldInvitations();
    boolean acceptInvitation(String invitationId);
}