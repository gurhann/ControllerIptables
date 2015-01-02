/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dursunc.bilgiguvenlik;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author dcimen
 */
@ManagedBean
@SessionScoped
public class connectSsh implements Serializable{
    private User user;
    private Session jschSession;
    private Channel channel;
    public connectSsh() throws JSchException {
        user = new User();
        int port = 22;
        JSch jsch = new JSch();
        jschSession = jsch.getSession(user.getUserName(), user.getIp(), port);
        jschSession.setUserInfo(new SshUserInfo(user.getPassWord()));
        jschSession.connect();                
        channel = jschSession.openChannel("exec");
        channel.connect();
        

    }
    
    public void loginSsh(){
        System.out.println(user.getIp()+" "+user.getUserName()+" "+user.getPassWord()+"dasdasd");
    
    }

    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }   
    
}
