package com.fh.entity.app;

import java.util.Date;

public class Censor {
    private String id;
    private String user_id;
    private Integer general_inspection;
    private Integer checked;
    private Integer total_score;
    private Integer score;
    private Integer total_issue;
    private Integer solved_issue;
    private Integer status;
    private Integer type;
    private Date created_time;
    private Date update_time;
    private String child_id;

    public String getChild_id() {
        return child_id;
    }

    public void setChild_id(String child_id) {
        this.child_id = child_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Integer getGeneral_inspection() {
        return general_inspection;
    }

    public void setGeneral_inspection(Integer general_inspection) {
        this.general_inspection = general_inspection;
    }

    public Integer getChecked() {
        return checked;
    }

    public void setChecked(Integer checked) {
        this.checked = checked;
    }

    public Integer getTotal_score() {
        return total_score;
    }

    public void setTotal_score(Integer total_score) {
        this.total_score = total_score;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getTotal_issue() {
        return total_issue;
    }

    public void setTotal_issue(Integer total_issue) {
        this.total_issue = total_issue;
    }

    public Integer getSolved_issue() {
        return solved_issue;
    }

    public void setSolved_issue(Integer solved_issue) {
        this.solved_issue = solved_issue;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getCreated_time() {
        return created_time;
    }

    public void setCreated_time(Date created_time) {
        this.created_time = created_time;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }
}
