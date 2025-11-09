package org.healthtrack.mapper;

import org.apache.ibatis.annotations.*;
import org.healthtrack.entity.FamilyGroup;
import java.util.List;

@Mapper
public interface FamilyGroupMapper {

    @Select("SELECT * FROM family_group")
    List<FamilyGroup> findAll();

    @Select("SELECT * FROM family_group WHERE family_id = #{familyId}")
    FamilyGroup findById(String familyId);

    @Insert("INSERT INTO family_group (family_id) VALUES (#{familyId})")
    int insert(FamilyGroup familyGroup);

    @Delete("DELETE FROM family_group WHERE family_id = #{familyId}")
    int delete(String familyId);
}