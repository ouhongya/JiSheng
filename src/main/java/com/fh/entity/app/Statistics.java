package com.fh.entity.app;

import java.util.List;

public class Statistics {
    private List<String> unitId;
    private List<String> userId;
    private List<String> excelId;
    private List<String> taskId;
    private String siteId;
    private Integer type;
    private Integer num;
    private Integer page;
    private Integer size;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public List<String> getTaskId() {
        return taskId;
    }

    public void setTaskId(List<String> taskId) {
        this.taskId = taskId;
    }

    public List<String> getUnitId() {
        return unitId;
    }

    public void setUnitId(List<String> unitId) {
        this.unitId = unitId;
    }

    public List<String> getUserId() {
        return userId;
    }

    public void setUserId(List<String> userId) {
        this.userId = userId;
    }

    public List<String> getExcelId() {
        return excelId;
    }

    public void setExcelId(List<String> excelId) {
        this.excelId = excelId;
    }
}
