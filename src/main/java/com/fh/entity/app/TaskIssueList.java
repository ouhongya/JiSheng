package com.fh.entity.app;

import java.util.List;

public class TaskIssueList {

    private String id;
    private String row_id;
    private String conent;
    private String type;
    private String score;
    private String STATUS;
    private String remark;
    private List<Issueimage> issueimages;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRow_id() {
        return row_id;
    }

    public void setRow_id(String row_id) {
        this.row_id = row_id;
    }

    public String getConent() {
        return conent;
    }

    public void setConent(String conent) {
        this.conent = conent;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<Issueimage> getIssueimages() {
        return issueimages;
    }

    public void setIssueimages(List<Issueimage> issueimages) {
        this.issueimages = issueimages;
    }
}
