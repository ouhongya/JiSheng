package com.fh.entity.requestVo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

public class    TaskVo {
    private String id;
    private String name;
    private String unitId;
    private String siteId;
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date starTime;
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date endTime;
    private String location;
    private String groupId;
    private Integer frequency;
    private Integer type;
    private String userId;
    List<TaskDetailUserVo> taskDetails;
    List<String> excelId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStarTime(Date starTime) {
        this.starTime = starTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getStarTime() {
        return starTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<TaskDetailUserVo> getTaskDetails() {
        return taskDetails;
    }

    public void setTaskDetails(List<TaskDetailUserVo> taskDetails) {
        this.taskDetails = taskDetails;
    }

    public List<String> getExcelId() {
        return excelId;
    }

    public void setExcelId(List<String> excelId) {
        this.excelId = excelId;
    }
}
