/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dursunc.bilgiguvenlik;

import com.jcraft.jsch.Channel;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author dcimen
 */
public class securityTools implements Serializable {

    private DataInputStream dataIn;
    private DataOutputStream dataOut;
     String endLineStr = " # "; 

    public securityTools() {
    }

    public void getBannedList(Channel ch) throws IOException {
        try {
            dataIn = new DataInputStream(ch.getInputStream());
            dataOut = new DataOutputStream(ch.getOutputStream());
            dataOut.writeBytes("ls -la");
            dataOut.flush();
            String line = dataIn.readLine();
            System.out.println(line);
            while (!line.endsWith(endLineStr)) {
                System.out.println(line);
                line = dataIn.readLine();
            }
        } catch (Exception e) {
        }

        
    }
}
