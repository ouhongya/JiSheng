package com.fh.entity.app;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class TaskDetail {
    private String id;
    private String task_id;
    private String censor_id;
    private String norm_id;
    private String norm_detail_id;
    private Integer total_score;
    private Integer score;
    private Integer total_issue;
    private Integer solved_issue;
    private Integer check_item;
    private Integer to_check;
    private Integer status;
    private String child_id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date created_time;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date update_time;

    public String getChild_id() {
        return child_id;
    }

    public void setChild_id(String child_id) {
        this.child_id = child_id;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getCensor_id() {
        return censor_id;
    }

    public void setCensor_id(String censor_id) {
        this.censor_id = censor_id;
    }

    public String getNorm_id() {
        return norm_id;
    }

    public void setNorm_id(String norm_id) {
        this.norm_id = norm_id;
    }

    public String getNorm_detail_id() {
        return norm_detail_id;
    }

    public void setNorm_detail_id(String norm_detail_id) {
        this.norm_detail_id = norm_detail_id;
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


    public Integer getCheck_item() {
        return check_item;
    }

    public void setCheck_item(Integer check_item) {
        this.check_item = check_item;
    }

    public Integer getTo_check() {
        return to_check;
    }

    public void setTo_check(Integer to_check) {
        this.to_check = to_check;
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
