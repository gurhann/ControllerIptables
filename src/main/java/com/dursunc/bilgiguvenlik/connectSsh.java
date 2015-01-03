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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    private java.util.Properties config;
    private User user;
    private Session jschSession;
    private Channel channel;
    private JSch jsch;
    private String endLineStr = " # ";
    private List<String> bannedList = new ArrayList<String>();

    public connectSsh() throws JSchException {
        user = new User();
        config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
    }

    public boolean isLogin() {
        return channel.isConnected();
    }

    public void bannedIpList() {
        String command = "sudo iptables -L INPUT | grep -o '[0-9]\\{1,3\\}\\.[0-9]\\{1,3\\}\\.[0-9]\\{1,3\\}\\.[0-9]\\{1,3\\}'";
        try {
            jschConnect();
            InputStream in = runCommand(command);
            String dizi[] = (read(in).split("\\r?\\n"));
            System.out.println(dizi.length);
            for (int i = 2; i < dizi.length; i++) {
                bannedList.add(dizi[i]);
            }
            connectOut();
        } catch (Exception e) {
            System.out.println("+++++++++++++" + e.toString());
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("Bağlantı Sağlanamadı", "Lütfen bilgilerinizi kontrol ediniz!"));
        }

    }

    public String loginSsh() throws JSchException {
        String url = "";
        try {
            jschConnect();

            connectOut();
            bannedIpList();
            url="main";
        } catch (Exception e) {
            System.out.println("+++++++++++++" + e.toString());
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("Bağlantı Sağlanamadı", "Lütfen bilgilerinizi kontrol ediniz!"));
        }
        return url;

    }

    public InputStream runCommand(String command) throws JSchException, IOException {
        ((ChannelExec) channel).setCommand(command);//Komut Fonksiyonu
        ((ChannelExec) channel).setPty(true);
        channel.setInputStream(null);
        ((ChannelExec) channel).setErrStream(System.err);
        InputStream in = channel.getInputStream();
        OutputStream out = channel.getOutputStream();
        channel.connect();
        out.write((user.getPassWord() + "\n").getBytes());
        out.flush();
        return in;
    }

    public String read(InputStream in) throws IOException {
        String retuned = null;
        byte[] tmp = new byte[1024];
        int count = 0;
        while (true) {
            while (in.available() > 0) {
                count = 0;
                int i = in.read(tmp, 0, 1024);
                if (i < 0) {
                    break;
                }
                retuned = retuned + (new String(tmp, 0, i));
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
        return retuned;
    }

    public boolean jschConnect() throws JSchException {
        jsch = new JSch();
        jschSession = jsch.getSession(user.getUserName(), user.getIp(), 22);
        jschSession.setPassword(user.getPassWord());
        jschSession.setUserInfo(new SshUserInfo(user.getPassWord()));
        jschSession.setConfig(config);
        jschSession.connect();
        channel = jschSession.openChannel("exec");
        return jschSession.isConnected();

    }

    public void connectOut() {
        if (channel.isConnected()) {
            channel.disconnect();
        }

        jschSession.disconnect();

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<String> getBannedList() {
        return bannedList;
    }

    public void setBannedList(List<String> bannedList) {
        this.bannedList = bannedList;
    }

}
