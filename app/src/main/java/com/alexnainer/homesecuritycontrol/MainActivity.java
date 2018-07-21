package com.alexnainer.homesecuritycontrol;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener  {

    public TCPClient tcpClient;
    private TCPNetworkingTask tcpNetworking;
    public CommandSender commandSender;
    public ColourAnimator colourAnimator;
    public DialogPresenter dialogPresenter;
    public ToastPresenter toastPresenter;
    private SharedPreferences prefs;

    boolean attemptingToConnect = false;
    boolean didLaunchSettings = false;

    public Context context;

    CardView armButton;
    CardView disarmButton;

    Toolbar toolbar;

    View statusView;
    TextView statusText;

    SwipeRefreshLayout pullToRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("LEARN", "+++ ON CREATE +++");

//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        context = getApplicationContext();

        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        pullToRefresh = findViewById(R.id.pullToRefresh);

        statusView = findViewById(R.id.statusView);
        statusText = findViewById(R.id.statusText);
        statusText.setTextColor(Color.parseColor("#000000"));


        armButton = findViewById(R.id.armButton);
        armButton.setOnClickListener(this);

        disarmButton = findViewById(R.id.disarmButton);
        disarmButton.setOnClickListener(this);
        statusText.setText("Not Connected");

        pullToRefresh.setRefreshing(false);

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                attemptingToConnect = true;
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {

                                if (attemptingToConnect) {
                                    pullToRefresh.setRefreshing(false);
                                    toastPresenter.showCannotConnectToast();
                                    Log.i("TCP", "Cannot connect to server");

                                    if (tcpClient != null) {
                                        tcpClient.stopClient();
                                        tcpClient = null;
                                    }

                                    statusView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                                    statusText.setText("Not Connected");

                                }
                            }
                        },
                        2000);

                attemptToConnect();

                Log.d("TCP", "attempting to connect...");

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("LEARN", "++ ON START ++");
    }

    @Override
    public void onResume() {
        super.onResume();

        boolean isAutoConnect = prefs.getBoolean("key_auto_connect", false);

        if(isAutoConnect && !didLaunchSettings){
            pullToRefresh.setRefreshing(true);
            attemptToConnect();
        }
        didLaunchSettings = false;

        colourAnimator = new ColourAnimator(this);
        commandSender = new CommandSender(this);
        dialogPresenter = new DialogPresenter(this);
        toastPresenter = new ToastPresenter(this);

        disableButtons();

        statusView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        statusText.setText("Not Connected");
        statusText.setTextColor(Color.parseColor("#000000"));

        Log.d("LEARN", "+ ON RESUME +");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("LEARN", "- ON PAUSE -");
    }

    @Override
    public void onRestart() {
        super.onRestart();
        Log.d("LEARN", "ON RESTART");
    }

    @Override
    public void onStop() {
        super.onStop();

        commandSender = null;
        dialogPresenter = null;
        colourAnimator = null;

        nullTCPObjects();

        Log.d("LEARN", "-- ON STOP --");
    }


    private void attemptToConnect() {
        tcpNetworking = new TCPNetworkingTask();
        tcpNetworking.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void nullTCPObjects() {

        if (tcpClient != null) {
            tcpClient.stopClient();
            tcpClient = null;
        }

        if (tcpNetworking != null) {
            tcpNetworking.cancel(true);
            tcpNetworking = null;
        }
    }

    private void disableButtons() {

        armButton.setClickable(false);
        disarmButton.setClickable(false);

        armButton.setBackgroundColor(ContextCompat.getColor(context, R.color.cardDisabled));
        disarmButton.setBackgroundColor(ContextCompat.getColor(context, R.color.cardDisabled));

        armButton.setElevation(0);
        disarmButton.setElevation(0);
    }

    private void enableButtons() {

        armButton.setClickable(true);
        disarmButton.setClickable(true);

        armButton.setBackgroundColor(ContextCompat.getColor(context, R.color.cardEnabled));
        disarmButton.setBackgroundColor(ContextCompat.getColor(context, R.color.cardEnabled));

        armButton.setElevation(8);
        disarmButton.setElevation(8);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settingsButton) {
            didLaunchSettings = true;
            startActivity(new Intent(MainActivity.this, SettingsPrefActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.armButton:
                Log.d("TCP", "arm onClick");
                toastPresenter.showSendArmToast();
                commandSender.sendArm(tcpClient);
                break;

            case R.id.disarmButton:
                Log.d("TCP", "disarm onClick");
                toastPresenter.showSendDisarmToast();
                commandSender.sendDisarm(tcpClient);
                break;

            default:
                break;

        }

    }

    public class TCPNetworkingTask extends AsyncTask<String, String, TCPClient> {

        @Override
        protected TCPClient doInBackground(String... message) {

            tcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
                @Override
                public void messageReceived(String message) {
                    publishProgress(message);
                }
            }, context);
            tcpClient.run();

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            Log.d("TCP", "Response from Server: " + values[0]);

            if (values[0].equals("Login:")) {
                attemptingToConnect = false;
                commandSender.sendLogin(tcpClient);
            } else if (values[0].equals("OK")) {

                statusText.setText("Getting Status...");
                toastPresenter.showSuccessToast();

            } else if (values[0].contains("****DISARMED****")) {

                enableButtons();

                toastPresenter.cancelSendDisarmToast();
                attemptingToConnect = false;

                statusText.setTextColor(Color.parseColor("#FFFFFF"));
                colourAnimator.toAlarmGreen(statusView);
                statusText.setText("Disarmed");
                pullToRefresh.setRefreshing(false);
                Log.d("TCP", "Alarm is disarmed");


            } else if (values[0].contains("ARMED ***STAY***")) {

                enableButtons();

                toastPresenter.cancelSendArmToast();
                attemptingToConnect = false;

                statusText.setTextColor(Color.parseColor("#FFFFFF"));
                statusText.setText("Armed (Stay)");
                colourAnimator.toAlarmRed(statusView);
                pullToRefresh.setRefreshing(false);
                Log.d("TCP", "Alarm is armed");


            } else if (values[0].contains("FAILED")) {
                pullToRefresh.setRefreshing(false);
                attemptingToConnect = false;

                disableButtons();

                dialogPresenter.showUnableToLoginDialog();

                nullTCPObjects();

            } else if (values[0].equals("Connection Error")) {
                pullToRefresh.setRefreshing(false);
                attemptingToConnect = false;

                disableButtons();

                dialogPresenter.showUnableToConnectDialog();

                nullTCPObjects();

            }
        }


    }


}