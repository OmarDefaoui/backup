package com.omardev.backuptest;

/**
 * Created by Omar on 30/03/2018.
 */

public class ListItemContact {
    String username,name,num;

    public ListItemContact(String username, String name, String num) {
        this.username = username;
        this.name = name;
        this.num = num;
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
}
