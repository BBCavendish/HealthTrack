package org.healthtrack.mapper;

import org.apache.ibatis.annotations.*;
import org.healthtrack.entity.WellnessChallenge;
import java.util.List;

@Mapper
public interface WellnessChallengeMapper {

    // 修正：使用正确的表名 wellness_challenge（带下划线）
    @Select("SELECT * FROM wellness_challenge")
    List<WellnessChallenge> findAll();

    @Select("SELECT * FROM wellness_challenge WHERE challenge_id = #{challengeId}")
    WellnessChallenge findById(String challengeId);

    @Insert("INSERT INTO wellness_challenge (challenge_id, goal, start_date, end_date, description, creator_id) " +
            "VALUES (#{challengeId}, #{goal}, #{startDate}, #{endDate}, #{description}, #{creatorId})")
    int insert(WellnessChallenge challenge);

    @Update("UPDATE wellness_challenge SET goal = #{goal}, start_date = #{startDate}, end_date = #{endDate}, " +
            "description = #{description}, creator_id = #{creatorId} WHERE challenge_id = #{challengeId}")
    int update(WellnessChallenge challenge);

    @Delete("DELETE FROM wellness_challenge WHERE challenge_id = #{challengeId}")
    int delete(String challengeId);

    @Select("SELECT * FROM wellness_challenge WHERE creator_id = #{creatorId}")
    List<WellnessChallenge> findByCreatorId(String creatorId);

    @Select("SELECT * FROM wellness_challenge WHERE start_date <= NOW() AND end_date >= NOW()")
    List<WellnessChallenge> findActiveChallenges();

    @Select("SELECT * FROM wellness_challenge WHERE end_date < NOW()")
    List<WellnessChallenge> findCompletedChallenges();
}