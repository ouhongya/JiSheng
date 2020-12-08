package com.fh.entity.requestVo;

import java.util.ArrayList;

public class TaskDetailUserVo {
    private String userId;
    private ArrayList<String>  module;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList<String> getModule() {
        return module;
    }

    public void setModule(ArrayList<String> module) {
        this.module = module;
    }
}
