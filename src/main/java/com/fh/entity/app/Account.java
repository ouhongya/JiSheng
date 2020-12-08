package com.fh.entity.app;

public class Account {


    private String  name;
    private String  username;
    private String  phone;
    private String  roleid;
    private String  accountfunction;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRoleid() {
        return roleid;
    }

    public void setRoleid(String roleid) {
        this.roleid = roleid;
    }

    public String getAccountfunction() {
        return accountfunction;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAccountfunction(String accountfunction) {
        this.accountfunction = accountfunction;
    }
}
