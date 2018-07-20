package com.alexnainer.homesecuritycontrol;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class CommandSender extends MainActivity {

    SharedPreferences prefs;
    String password;
    String pin;

    public CommandSender(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        password = prefs.getString("key_password", "DEFAULT");
        pin = prefs.getString("key_pin", "DEFAULT");

    }



     public void sendLogin(TCPClient tcpClient) {

        if(tcpClient != null) {
            Log.d("TCP", "Sending password...");
            tcpClient.sendMessage(password);
        }
    }

    public void sendArm(TCPClient tcpClient) {

        if (tcpClient != null) {
            Log.d("TCP", "Attempting to Arm...");
            tcpClient.sendMessage("^3," + pin + "3$");
        }
    }

    public void sendDisarm(TCPClient tcpClient) {

        if(tcpClient != null) {
            Log.d("TCP", "Attempting to Disarm...");
            tcpClient.sendMessage("^3," + pin + "1$");
        }

    }


}