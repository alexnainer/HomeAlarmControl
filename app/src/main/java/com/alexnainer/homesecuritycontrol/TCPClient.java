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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class TCPClient {


    public final String TAG = TCPClient.class.getSimpleName();
    SharedPreferences prefs;
    public static String serverIP;
    public static final int serverPort = 4025;
    private String mServerMessage;
    private OnMessageReceived mMessageListener = null;
    private boolean isConnected = false;
    private PrintWriter mBufferOut;
    private BufferedReader mBufferIn;
    private Socket socket;
    private int maxConnectionAttempts;
    private int currentConnectionAttempt = 0;

    int x = 0;


    public TCPClient(OnMessageReceived listener, Context context) {

        mMessageListener = listener;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        serverIP = prefs.getString("key_ip_address", "DEFAULT");
        maxConnectionAttempts = prefs.getInt("key_connection_attempts", 1);
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

        isConnected = false;

        if (mBufferOut != null) {
            mBufferOut.flush();
            mBufferOut.close();
        }


        mBufferIn = null;
        mBufferOut = null;
        mServerMessage = null;
    }

    public void run() {


        boolean isTimeout = false;
        boolean isConnectionReset = false;
        currentConnectionAttempt = 0;


        try {
            InetSocketAddress serverAddress = new InetSocketAddress(serverIP, serverPort);



            while (!isConnected && (currentConnectionAttempt < maxConnectionAttempts)) {

                try {
                    Log.d("TCP Client", "C: Connecting...");

                    currentConnectionAttempt++;
                    socket = new Socket();
                    socket.connect(serverAddress, 1000);


                    mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                    mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    mServerMessage = mBufferIn.readLine();
                    if (mServerMessage != null && mMessageListener != null) {
                        mMessageListener.messageReceived(mServerMessage);
                    }

                    isConnected = true;
                    isConnectionReset = false;
                    isTimeout = false;

                } catch (SocketException e) {

                    Log.e("TCP", "S: Will Attempt to Connect Again", e);
                    isConnectionReset = true;
                    socket.close();
                    stopClient();

                    Thread.sleep(3000);
                } catch (SocketTimeoutException e) {
                    Log.e("TCP", "S: Connection Timeout", e);
                    isTimeout = true;

                    socket.close();
                    stopClient();
                }
            }

            if (isTimeout) {
                if (mMessageListener != null) {
                    mMessageListener.messageReceived("Connection Timeout");
                }
            } else if (isConnectionReset) {
                if (mMessageListener != null) {
                    mMessageListener.messageReceived("Connection Reset");
                }
            } else {
                try {
                    while (isConnected) {

                        mServerMessage = mBufferIn.readLine();

                        if (mServerMessage != null && mMessageListener != null) {
                            mMessageListener.messageReceived(mServerMessage);
                        }
                    }


                } catch (Exception e) {
                    Log.e("TCP", "S: Error1", e);


                } finally {
                    socket.close();
                    stopClient();

                }
            }



        } catch (Exception e) {
            Log.e("TCP", "C: Error2", e);
        }

    }

    public interface OnMessageReceived {
        public void messageReceived(String message);
    }

}
