package com.fh.entity.app;

import java.util.List;

public class Module {
    private String userId;
    private List<String> module;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getModule() {
        return module;
    }

    public void setModule(List<String> module) {
        this.module = module;
    }
}
