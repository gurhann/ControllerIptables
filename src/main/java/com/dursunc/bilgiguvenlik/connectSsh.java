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
import java.io.DataInputStream;
import java.io.DataOutputStream;
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
            int port = 22;
            JSch jsch = new JSch();
            jschSession = jsch.getSession(user.getUserName(), user.getIp(), port);
            jschSession.setPassword(user.getPassWord());
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            jschSession.setConfig(config);
            jschSession.connect();
            channel = jschSession.openChannel("sftp");
            channel.connect();
            DataInputStream dataIn = new DataInputStream(channel.getInputStream());
            DataOutputStream dataOut = new DataOutputStream(channel.getOutputStream());

            // send ls command to the server  
            dataOut.writeBytes("ls -la\r\n");
            dataOut.flush();

            // and print the response   
            String line = dataIn.readLine();
            System.out.println(line);
            while (!line.endsWith(endLineStr)) {
                System.out.println(line);
                line = dataIn.readLine();
            }

        } catch (Exception e) {
            System.out.println("+++++++++++++" + e);
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
