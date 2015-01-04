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
    private List<BannedIp> bannedList;
    private String bannedIp;

    public connectSsh() throws JSchException {

        user = new User();
        config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
    }

    public boolean isLogin() {
        return channel.isConnected();
    }

    public void bannedIpList() {
        String command = "sudo iptables -L INPUT";
        try {
            jschConnect();
            InputStream in = runCommand(command);
            bannedList = new ArrayList<BannedIp>();

            String rows[] = (read(in).split("\\r?\\n"));
            for (int i = 0; i < rows.length; i++) {
                System.out.println(i + "-> " + rows[i]);
            }
            System.out.println("--------------------------");
            bannedList = new ArrayList<BannedIp>();
            for (int i = 0; i < rows.length; i++) {
                if (!rows[i].contains("DROP")) {
                    continue;
                }
                List<String> row = Arrays.asList(rows[i].split(" "));
                List<String> correctRow = new ArrayList<>();
                for(int j = 0; j < row.size(); j++) {
                    if (row.get(j).trim().equals("")){
                        continue;
                    }
                    correctRow.add(row.get(j));
                }
                BannedIp bannedItem = new BannedIp();
                bannedItem.setProt(correctRow.get(1));
                bannedItem.setIp(correctRow.get(3));
                try {
                bannedItem.setPort(correctRow.get(6).substring(correctRow.get(6).indexOf(":")+1));
                }catch(Exception e) {
                    bannedItem.setPort("all");
                }
                bannedList.add(bannedItem);
                
            }
            connectOut();
        } catch (Exception e) {
            e.printStackTrace();
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
            url = "main";
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("Bağlantı Sağlanamadı", "Lütfen bilgilerinizi kontrol ediniz!"));
        }
        return url;

    }

    public void ipReBan(String ip, String port) throws JSchException, IOException {
        String command;
        if(port.equals("all")){
             command= "sudo iptables -D INPUT -s " + ip + " -j DROP";
        }else {
            command = "sudo iptables -D INPUT -s " + ip + " -p tcp  --destination-port " + port + " -j DROP";
        }
        try {
            System.out.println("----------->" + command);
            jschConnect();
            InputStream in = runCommand(command);
            System.out.println(read(in));
            connectOut();
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("IP Ban Kaldırıldı.", "Banlanan Ip listesine bakabilirsiniz."));
            bannedIpList();
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("Sorun Var", "Ip ban kaldırılırken bir sorun çıktı"));
        }
    }

    public void ipBan() throws JSchException, IOException {
        String command = "sudo iptables -A INPUT -s " + bannedIp + " -j DROP";
        try {
            System.out.println("----------->" + command);
            jschConnect();
            InputStream in = runCommand(command);
            System.out.println(read(in));
            connectOut();
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("IP Banlandı", "Banlanan Ip listesine bakabilirsiniz."));
            bannedIpList();

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("Sorun Var", "Ip banlanırken bir sorun çıktı"));
        }
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

    public List<BannedIp> getBannedList() {
        return bannedList;
    }

    public void setBannedList(List<BannedIp> bannedList) {
        this.bannedList = bannedList;
    }

    public String getBannedIp() {
        return bannedIp;
    }

    public void setBannedIp(String bannedIp) {
        this.bannedIp = bannedIp;
    }

}
