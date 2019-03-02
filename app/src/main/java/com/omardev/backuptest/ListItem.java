package com.omardev.backuptest;

/**
 * Created by Omar on 27/03/2018.
 */

public class ListItem {
    String id,name,passsword;

    public ListItem(String id, String name, String passsword) {
        this.id = id;
        this.name = name;
        this.passsword = passsword;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasssword() {
        return passsword;
    }

    public void setPasssword(String passsword) {
        this.passsword = passsword;
    }
}
