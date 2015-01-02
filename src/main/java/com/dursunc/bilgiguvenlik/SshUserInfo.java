/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dursunc.bilgiguvenlik;

import com.jcraft.jsch.UserInfo;

/**
 *
 * @author gurhan
 */
public class SshUserInfo implements UserInfo {

    String password;

    public SshUserInfo(String password) {
        this.password = password;
    }

    @Override
    public String getPassphrase() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean promptPassword(String arg0) {
        return true;
    }

    @Override
    public boolean promptPassphrase(String arg0) {
        return true;
    }

    @Override
    public boolean promptYesNo(String arg0) {
        return true;
    }

    @Override
    public void showMessage(String arg0) {
        System.out.println(arg0);
    }

}
