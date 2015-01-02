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
public class ipInfo implements Serializable{
    private int id;
    private String ip;
    private String port;
    private boolean ban;

    public ipInfo() {
    }

    public ipInfo(int id, String ip, String port, boolean ban) {
        this.id = id;
        this.ip = ip;
        this.port = port;
        this.ban = ban;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public boolean isBan() {
        return ban;
    }

    public void setBan(boolean ban) {
        this.ban = ban;
    }
    
}
