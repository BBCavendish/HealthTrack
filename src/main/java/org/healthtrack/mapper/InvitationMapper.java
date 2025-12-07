package org.healthtrack.mapper;

import org.apache.ibatis.annotations.*;
import org.healthtrack.entity.Invitation;
import java.util.List;

@Mapper
public interface InvitationMapper {

    @Select("SELECT * FROM invitation")
    List<Invitation> findAll();

    @Select("SELECT * FROM invitation WHERE invitation_id = #{invitationId}")
    Invitation findById(String invitationId);

    @Insert("INSERT INTO invitation (invitation_id, invitee_contact, sent_time, expired_time, status, " +
            "invitation_type, inviter_id, related_challenge_id) " +
            "VALUES (#{invitationId}, #{inviteeContact}, #{sentTime}, #{expiredTime}, #{status}, " +
            "#{invitationType}, #{inviterId}, #{relatedChallengeId})")
    int insert(Invitation invitation);

    @Update("UPDATE invitation SET invitee_contact = #{inviteeContact}, sent_time = #{sentTime}, " +
            "expired_time = #{expiredTime}, status = #{status}, invitation_type = #{invitationType}, " +
            "inviter极_id = #{inviterId}, related_challenge_id = #{relatedChallengeId} " +
            "WHERE invitation_id = #{invitationId}")
    int update(Invitation invitation);

    @Delete("DELETE FROM invitation WHERE invitation_id = #{invitationId}")
    int delete(String invitationId);

    @Select("SELECT * FROM invitation WHERE inviter_id = #{inviterId}")
    List<Invitation> findByInviterId(String inviterId);

    @Select("SELECT * FROM invitation WHERE status = #{status}")
    List<Invitation> findByStatus(String status);

    @Select("SELECT * FROM invitation WHERE expired_time < NOW() AND status = 'Pending'")
    List<Invitation> findExpiredInvitations();

    @Select("SELECT * FROM invitation WHERE invitee_contact = #{inviteeContact}")
    List<Invitation> findByInviteeContact(String inviteeContact);
}