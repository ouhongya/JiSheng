package com.fh.entity.app;

import java.util.List;

public class RoleAppVo {
    private String name;
    private String roleFunction;
    private List<RoleCheckVo> rolecheckList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoleFunction() {
        return roleFunction;
    }

    public void setRoleFunction(String roleFunction) {
        this.roleFunction = roleFunction;
    }

    public List<RoleCheckVo> getRolecheckList() {
        return rolecheckList;
    }

    public void setRolecheckList(List<RoleCheckVo> rolecheckList) {
        this.rolecheckList = rolecheckList;
    }
}
