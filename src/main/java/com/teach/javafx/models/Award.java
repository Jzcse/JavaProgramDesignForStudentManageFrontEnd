package com.teach.javafx.models;

import java.util.List;

public class Award {
    private String awardId;
    private String awardName;
    private String awardLevel;
    private String awardTime;
    private String awardSize;
    private List<AwardPerson> awardStudentList;
    public Award() {

    }
    public Award(String awardId, String awardName) {
        this.awardId = awardId;
        this.awardName = awardName;
    }

    public String getAwardId() {
        return awardId;
    }

    public void setAwardId(String awardId) {
        this.awardId = awardId;
    }

    public String getAwardName() {
        return awardName;
    }

    public void setAwardName(String awardName) {
        this.awardName = awardName;
    }

    public String getAwardLevel() {
        return awardLevel;
    }

    public void setAwardLevel(String awardLevel) {
        this.awardLevel = awardLevel;
    }

    public String getAwardTime() {
        return awardTime;
    }

    public void setAwardTime(String awardTime) {
        this.awardTime = awardTime;
    }

    public String getAwardSize() {
        return awardSize;
    }

    public void setAwardSize(String awardSize) {
        this.awardSize = awardSize;
    }

    public List<AwardPerson> getAwardStudentList() {
        return awardStudentList;
    }

    public void setAwardStudentList(List<AwardPerson> awardStudentList) {
        this.awardStudentList = awardStudentList;
    }
}

