package com.alexnainer.homesecuritycontrol;

import android.app.Activity;
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

import static java.security.AccessController.getContext;


public class TCPClient {


    public final String TAG = TCPClient.class.getSimpleName();
    SharedPreferences prefs;
    public static String serverIP; //server IP address
    public static final int SERVER_PORT = 4025;
    // message to send to the server
    private String mServerMessage;
    // sends message received notifications
    private OnMessageReceived mMessageListener = null;
    // while this is true, the server will continue running
    private boolean shouldSocketBeOpen = false;
    // used to send messages
    private PrintWriter mBufferOut;
    // used to read messages from the server
    private BufferedReader mBufferIn;
    private Socket socket;

    public boolean unableToConnectError;

    private OnErrorListener onErrorListener;

    /**
     * Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TCPClient(OnMessageReceived listener, Context context) {

        mMessageListener = listener;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        unableToConnectError = false;
        serverIP = prefs.getString("key_ip_address", "DEFAULT");

    }

    public void setOnErrorListener(OnErrorListener listener) {
        onErrorListener = listener;
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

    /**
     * Close the connection and release the members
     */
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
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(serverIP);

            Log.d("TCP Client", "C: Connecting...");

            //create a socket to make the connection with the server
            socket = new Socket(serverAddr, SERVER_PORT);


            try {


                //sends the message to the server
                mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                //receives the message which the server sends back
                mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                //in this while the client listens for the messages sent by the server
                while (shouldSocketBeOpen) {

                    unableToConnectError = false;

                    mServerMessage = mBufferIn.readLine();

                    if (mServerMessage != null && mMessageListener != null) {
                        //call the method messageReceived from MyActivity class
                        mMessageListener.messageReceived(mServerMessage);
                        //Log.d("TCP Client", "S: Received Message: '" + mServerMessage + "'");
                    }

                }


            } catch (Exception e) {
                Log.e("TCP", "S: Error1", e);

                if (onErrorListener != null) {
                    onErrorListener.onConnectionError();
                }


            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                socket.close();
            }

        } catch (Exception e) {
            Log.e("TCP", "C: Error2", e);
        }

    }


    //Declare the interface. The method messageReceived(String message) will must be implemented in the Activity
    //class at on AsyncTask doInBackground
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }




}
