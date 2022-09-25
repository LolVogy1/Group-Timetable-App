package com.example.grouptimetable;

import java.util.ArrayList;

public class User {
    public String username, password, sharecode;

    public User(){

    }
    public User(String user, String pass, String code){
        this.username = user;
        this.password = pass;
        this.sharecode = code;
    }


    public String getUsername(){
        return username;
    }
    public String getPassword(){
        return password;
    }
    public String getSharecode(){
        return sharecode;
    }
}
