package com.fh.entity.app;

import java.util.List;

public class DetailList {
    private String norm_detail_id;
    private String total_score;
    private String score;
    private List<DetailIssueList> issueList;

    public String getTotal_score() {
        return total_score;
    }

    public void setTotal_score(String total_score) {
        this.total_score = total_score;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getNorm_detail_id() {
        return norm_detail_id;
    }

    public void setNorm_detail_id(String norm_detail_id) {
        this.norm_detail_id = norm_detail_id;
    }

    public List<DetailIssueList> getIssueList() {
        return issueList;
    }

    public void setIssueList(List<DetailIssueList> issueList) {
        this.issueList = issueList;
    }
}
