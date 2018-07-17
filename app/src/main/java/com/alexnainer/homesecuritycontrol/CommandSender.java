package com.alexnainer.homesecuritycontrol;

import android.util.Log;

public class CommandSender extends MainActivity {


    public void sendLogin(TCPClient tcpClient, String password) {

        if(tcpClient != null) {
            Log.d("TCP", "Sending password...");
            tcpClient.sendMessage("");
        }
    }

    public void sendArm(TCPClient tcpClient) {

        if (tcpClient != null) {
            Log.d("TCP", "Attempting to Arm...");
            tcpClient.sendMessage("");
        }
    }

    public void sendDisarm(TCPClient tcpClient) {

        if(tcpClient != null) {
            Log.d("TCP", "Attempting to Disarm...");
            tcpClient.sendMessage("");
        }

    }


}