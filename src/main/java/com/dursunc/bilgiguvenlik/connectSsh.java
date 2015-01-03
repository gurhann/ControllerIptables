/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dursunc.bilgiguvenlik;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author dcimen
 */
@ManagedBean
@SessionScoped
public class connectSsh implements Serializable {

    private securityTools tools = new securityTools();
    private User user;
    private Session jschSession;
    private Channel channel;
    String endLineStr = " # ";

    public connectSsh() throws JSchException {
        user = new User();
    }

    public void loginSsh() throws JSchException {
        try {
             
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            JSch jsch = new JSch();
             jschSession=jsch.getSession(user.getUserName(), user.getIp(), 22);
            jschSession.setPassword(user.getPassWord());
            jschSession.setUserInfo(new SshUserInfo(user.getPassWord()));
            jschSession.setConfig(config);
            jschSession.connect();
            System.out.println("Connected");
             
             channel=jschSession.openChannel("exec");
            ((ChannelExec)channel).setCommand("sudo chmod 777 -R /home/gurhan/fal");
            
            ((ChannelExec)channel).setPty(true);
            channel.setInputStream(null);
            ((ChannelExec)channel).setErrStream(System.err);
             
            InputStream in=channel.getInputStream();
            OutputStream out = channel.getOutputStream();
            channel.connect();
            out.write((user.getPassWord()+"\n").getBytes());
            out.flush();
   
            
            System.out.println(in.available());
            byte[] tmp = new byte[1024];
            int count = 0;
            while (true) {
                System.out.println(in.available());
                while (in.available() > 0) {
                    count = 0;
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    System.out.print(new String(tmp, 0, i));
                }if(in.available() == 0) {
                    count++;
                }if(count == 5) {
                    channel.disconnect();
                    break;
                }
                if (channel.isClosed()) {
                    System.out.println("exit-status: " + channel.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }
            channel.disconnect();
            jschSession.disconnect();
            System.out.println("DONE");
        } catch (Exception e) {
            System.out.println("+++++++++++++" + e.toString());
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("Bağlantı Sağlanamadı", "Lütfen bilgilerinizi kontrol ediniz!"));
        }

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
