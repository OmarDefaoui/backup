package com.omardev.backuptest;

public class ListItemCallLog {
    String username, name, num, duration, callType, callDate;

    public ListItemCallLog(String username, String name, String num, String duration, String callType, String callDate) {
        this.username = username;
        this.name = name;
        this.num = num;
        this.duration = duration;
        this.callType = callType;
        this.callDate = callDate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getCallDate() {
        return callDate;
    }

    public void setCallDate(String callDate) {
        this.callDate = callDate;
    }
}
