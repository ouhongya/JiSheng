package com.fh.entity.app;

import java.util.List;

public class DetailRow {
    private String task_detail_id;
    private String norm_id;
    private String norm_detail_id;
    private List<Row> rowList;

    public String getTask_detail_id() {
        return task_detail_id;
    }

    public void setTask_detail_id(String task_detail_id) {
        this.task_detail_id = task_detail_id;
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

    public List<Row> getRowList() {
        return rowList;
    }

    public void setRowList(List<Row> rowList) {
        this.rowList = rowList;
    }
}
