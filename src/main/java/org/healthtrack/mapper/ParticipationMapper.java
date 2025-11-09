package org.healthtrack.mapper;

import org.apache.ibatis.annotations.*;
import org.healthtrack.entity.Participation;
import java.util.List;

@Mapper
public interface ParticipationMapper {

    /**
     * 查询所有参与记录
     */
    @Select("SELECT * FROM participation")
    List<Participation> findAll();

    /**
     * 根据用户ID查询参与记录
     */
    @Select("SELECT * FROM participation WHERE health_id = #{healthId}")
    List<Participation> findByUserId(String healthId);

    /**
     * 根据挑战ID查询参与记录
     */
    @Select("SELECT * FROM participation WHERE challenge_id = #{challengeId}")
    List<Participation> findByChallengeId(String challengeId);

    /**
     * 查询特定用户参与特定挑战的记录
     */
    @Select("SELECT * FROM participation WHERE health_id = #{healthId} AND challenge_id = #{challengeId}")
    Participation findByUserAndChallenge(@Param("healthId") String healthId, @Param("challengeId") String challengeId);

    /**
     * 插入新的参与记录
     */
    @Insert("INSERT INTO participation (health_id, challenge_id, progress) VALUES (#{healthId}, #{challengeId}, #{progress})")
    int insert(Participation participation);

    /**
     * 更新参与进度
     */
    @Update("UPDATE participation SET progress = #{progress} WHERE health_id = #{healthId} AND challenge_id = #{challengeId}")
    int update(Participation participation);

    /**
     * 删除参与记录
     */
    @Delete("DELETE FROM participation WHERE health_id = #{healthId} AND challenge_id = #{challengeId}")
    int delete(@Param("healthId") String healthId, @Param("challengeId") String challengeId);

    /**
     * 统计挑战的参与人数
     */
    @Select("SELECT COUNT(*) FROM participation WHERE challenge_id = #{challengeId}")
    int countParticipantsByChallenge(String challengeId);

    /**
     * 统计用户参与的挑战数量
     */
    @Select("SELECT COUNT(*) FROM participation WHERE health_id = #{healthId}")
    int countChallengesByUser(String healthId);

    /**
     * 查询进度超过指定值的参与记录
     */
    @Select("SELECT * FROM participation WHERE progress >= #{minProgress}")
    List<Participation> findByProgressGreaterThanEqual(Integer minProgress);

    /**
     * 查询已完成（进度=100）的参与记录
     */
    @Select("SELECT * FROM participation WHERE progress = 100")
    List<Participation> findCompletedParticipations();

    @Select("SELECT COUNT(*) FROM participation WHERE challenge_id = #{challengeId}")
    int countByChallengeId(String challengeId);


}