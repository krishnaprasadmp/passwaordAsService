package com.braincorp.com.passwordasservice.model;

import java.util.ArrayList;

public class Groups {

    private String name;
    private long gid;
    private ArrayList<String> members;

    public Groups(String name, long gid, ArrayList<String> members) {
        this.name = name;
        this.gid = gid;
        this.members = members;
    }

    public Groups(String name, long gid) {
        this.name = name;
        this.gid = gid;
    }

    public Groups() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getGid() {
        return gid;
    }

    public void setGid(long gid) {
        this.gid = gid;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }

    @Override
    public String toString() {
        return "Groups{" +
                "name='" + name + '\'' +
                ", gid='" + gid + '\'' +
                ", members=" + members +
                '}';
    }
}
