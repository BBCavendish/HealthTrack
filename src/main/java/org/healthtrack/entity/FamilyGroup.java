package org.healthtrack.entity;

public class FamilyGroup {
    private String familyId;

    public FamilyGroup() {}

    public FamilyGroup(String familyId) {
        this.familyId = familyId;
    }

    public String getFamilyId() { return familyId; }
    public void setFamilyId(String familyId) { this.familyId = familyId; }
}