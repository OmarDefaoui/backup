package com.omardev.backuptest;

public class ListItemSms {

    String username,num,body,date,type;

    public ListItemSms(String username, String num, String body, String date, String type) {
        this.username = username;
        this.num = num;
        this.body = body;
        this.date = date;
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
