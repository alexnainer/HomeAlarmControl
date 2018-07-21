package com.alexnainer.homesecuritycontrol;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class TCPClient {


    public final String TAG = TCPClient.class.getSimpleName();
    SharedPreferences prefs;
    public static String serverIP;
    public static final int serverPort = 4025;
    private String mServerMessage;
    private OnMessageReceived mMessageListener = null;
    private boolean shouldSocketBeOpen = false;
    private PrintWriter mBufferOut;
    private BufferedReader mBufferIn;
    private Socket socket;

    public boolean unableToConnectError;

    public TCPClient(OnMessageReceived listener, Context context) {

        mMessageListener = listener;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        unableToConnectError = false;
        serverIP = prefs.getString("key_ip_address", "DEFAULT");
    }


    public void sendMessage(final String message) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (mBufferOut != null) {
                    Log.d(TAG, "Sending: " + message);
                    mBufferOut.println(message);
                    mBufferOut.flush();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();

    }

    public void stopClient() {

        shouldSocketBeOpen = false;

        if (mBufferOut != null) {
            mBufferOut.flush();
            mBufferOut.close();
        }

        mMessageListener = null;
        mBufferIn = null;
        mBufferOut = null;
        mServerMessage = null;
    }

    public void run() {

        shouldSocketBeOpen = true;

        try {
            InetAddress serverAddress = InetAddress.getByName(serverIP);

            Log.d("TCP Client", "C: Connecting...");

            socket = new Socket(serverAddress, serverPort);

            try {

                mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                while (shouldSocketBeOpen) {

                    unableToConnectError = false;

                    mServerMessage = mBufferIn.readLine();

                    if (mServerMessage != null && mMessageListener != null) {
                        mMessageListener.messageReceived(mServerMessage);
                    }

                }


            } catch (Exception e) {
                Log.e("TCP", "S: Error1", e);

                if (mMessageListener != null) {
                    mMessageListener.messageReceived("Connection Error");
                }


            } finally {
                socket.close();
            }

        } catch (Exception e) {
            Log.e("TCP", "C: Error2", e);
        }

    }

    public interface OnMessageReceived {
        public void messageReceived(String message);
    }

}
