/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dursunc.bilgiguvenlik;

import java.io.Serializable;

/**
 *
 * @author dcimen
 */
public class User implements Serializable {
    private String ip;
    private String userName;
    private String passWord;

    public User() {
    }

    public User(String ip, String userName, String passWord) {
        this.ip = ip;
        this.userName = userName;
        this.passWord = passWord;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }
    
    
}
