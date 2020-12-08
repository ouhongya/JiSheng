package com.fh.entity.app;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

public class IssueListVo {
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date starTime;
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date endTime;
    private String content;
    private String uid;
    private List<String> id;
    private List<issueList> issueDetail;

    public Date getStarTime() {
        return starTime;
    }

    public void setStarTime(Date starTime) {
        this.starTime = starTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<String> getId() {
        return id;
    }

    public void setId(List<String> id) {
        this.id = id;
    }

    public List<issueList> getIssueDetail() {
        return issueDetail;
    }

    public void setIssueDetail(List<issueList> issueDetail) {
        this.issueDetail = issueDetail;
    }
}
