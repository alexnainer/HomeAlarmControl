package com.alexnainer.homealarmcontrol;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class CommandSender {

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


    public void sendDisarm(TCPClient tcpClient) {

        if(tcpClient != null) {
            Log.d("TCP", "Attempting to Disarm...");
            tcpClient.sendMessage("^3," + pin + "1$");
        }

    }

    public void sendArmStay(TCPClient tcpClient) {

        if (tcpClient != null) {
            Log.d("TCP", "Attempting to Arm...");
            tcpClient.sendMessage("^3," + pin + "3$");
        }
    }

    public void sendArmAway(TCPClient tcpClient) {

        if (tcpClient != null) {
            Log.d("TCP", "Attempting to Arm...");
            tcpClient.sendMessage("^3," + pin + "2$");
        }
    }



    public void sendPoll(TCPClient tcpClient) {
        Log.d("TCP", "Polling...");
        tcpClient.sendMessage("^0,$");
    }


}