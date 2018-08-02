package com.alexnainer.homesecuritycontrol;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class SecurityControlActivity extends AppCompatActivity implements View.OnClickListener  {

    private TCPClient tcpClient;
    private TCPNetworkingTask tcpNetworking;
    private CommandSender commandSender;
    private ColourAnimator colourAnimator;
    private DialogPresenter dialogPresenter;
    private ToastPresenter toastPresenter;
    private SharedPreferences prefs;

    private boolean didLaunchSettings = false;
    private boolean isConnected = false;

    private int DISCONNECTED = 0;
    private int DISARMED = 1;
    private int ARMED_STAY = 2;
    private int ARMED_AWAY = 3;

    private int currentStatus = DISCONNECTED;

    public Context context;

    CardView disarmButton;
    CardView armStayButton;
    CardView armAwayButton;


    View dividerDisarmArm;
    View dividerArmStayAway;

    Toolbar toolbar;

    View statusView;
    TextView statusText;
    TextView armAwayText;

    SwipeRefreshLayout pullToRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("LEARN", "+++ ON CREATE +++");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < 23) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.grey_700));
            getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(this, R.color.grey_50));
        }


        context = getApplicationContext();

        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        pullToRefresh = findViewById(R.id.pullToRefresh);

        statusView = findViewById(R.id.statusView);
        statusText = findViewById(R.id.statusText);
        statusText.setTextColor(Color.parseColor("#000000"));


        disarmButton = findViewById(R.id.disarmButton);
        disarmButton.setOnClickListener(this);

        armStayButton = findViewById(R.id.armStayButton);
        armStayButton.setOnClickListener(this);

        armAwayButton = findViewById(R.id.armAwayButton);
        armAwayButton.setOnClickListener(this);
        armAwayText = findViewById(R.id.armAwayText);

        dividerDisarmArm = findViewById(R.id.dividerBetweenArmDisarm);
        dividerArmStayAway = findViewById(R.id.dividerBetweenArmStayAway);

        statusText.setText("Not Connected");

        pullToRefresh.setRefreshing(false);

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                attemptToConnect();
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
        boolean showArmAwayButton = prefs.getBoolean("key_show_arm_away", true);

        if(showArmAwayButton) {
            armAwayButton.setVisibility(View.VISIBLE);
            dividerArmStayAway.setVisibility(View.VISIBLE);
            armAwayText.setVisibility(View.VISIBLE);
        } else {
            armAwayButton.setVisibility(View.GONE);
            dividerArmStayAway.setVisibility(View.GONE);
            armAwayText.setVisibility(View.GONE);
        }

        if(isAutoConnect && !didLaunchSettings){
            pullToRefresh.setRefreshing(true);
            startNetworkingTask();
        }
        didLaunchSettings = false;
        currentStatus = DISCONNECTED;

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

        pullToRefresh.setRefreshing(false);
        commandSender = null;
        dialogPresenter = null;
        colourAnimator = null;

        isConnected = false;
        nullTCPObjects();

        Log.d("LEARN", "-- ON STOP --");
    }


    private void startNetworkingTask() {
        Log.d("TCP", "Attempting to connect...");
        tcpNetworking = new TCPNetworkingTask();
        tcpNetworking.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void attemptToConnect() {

        if (prefs.getString("key_ip_address", "").equals("")) {
            pullToRefresh.setRefreshing(false);
            dialogPresenter.showNoIPDialog();

        } else if (isConnected) {
            if (tcpClient != null) {
                commandSender.sendPoll(tcpClient);
            }
        } else {
            startNetworkingTask();
        }
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

        armStayButton.setClickable(false);
        armAwayButton.setClickable(false);
        disarmButton.setClickable(false);

        armStayButton.setBackgroundColor(ContextCompat.getColor(context, R.color.cardDisabled));
        armAwayButton.setBackgroundColor(ContextCompat.getColor(context, R.color.cardDisabled));
        disarmButton.setBackgroundColor(ContextCompat.getColor(context, R.color.cardDisabled));

        armStayButton.setElevation(0);
        armAwayButton.setElevation(0);
        disarmButton.setElevation(0);

        dividerDisarmArm.setElevation(1);
        dividerArmStayAway.setElevation(1);
    }

    private void enableButtons() {

        armStayButton.setClickable(true);
        armAwayButton.setClickable(true);
        disarmButton.setClickable(true);

        armStayButton.setBackgroundColor(ContextCompat.getColor(context, R.color.cardEnabled));
        armAwayButton.setBackgroundColor(ContextCompat.getColor(context, R.color.cardEnabled));
        disarmButton.setBackgroundColor(ContextCompat.getColor(context, R.color.cardEnabled));

        armStayButton.setElevation(6);
        armAwayButton.setElevation(6);
        disarmButton.setElevation(6);

        dividerDisarmArm.setElevation(7);
        dividerArmStayAway.setElevation(7);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settingsButton) {
            didLaunchSettings = true;
            startActivity(new Intent(SecurityControlActivity.this, SettingsPrefActivity.class));
            return true;
        } else if (id == R.id.refresh_button) {
            pullToRefresh.setRefreshing(true);
            attemptToConnect();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.armStayButton:
                Log.d("TCP", "arm onClick");
                toastPresenter.showSendArmToast();
                commandSender.sendArm(tcpClient);
                pullToRefresh.setRefreshing(true);
                disableButtons();
                break;

            case R.id.disarmButton:
                Log.d("TCP", "disarm onClick");
                toastPresenter.showSendDisarmToast();
                commandSender.sendDisarm(tcpClient);
                pullToRefresh.setRefreshing(true);
                disableButtons();
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
                isConnected = true;
                commandSender.sendLogin(tcpClient);
            } else if (values[0].equals("OK")) {
                isConnected = true;
                statusText.setText("Getting Status...");
                toastPresenter.showSuccessToast();

            } else if (values[0].contains("****DISARMED****")) {
                isConnected = true;
                toastPresenter.cancelSendDisarmToast();

                if (currentStatus != DISARMED) {
                    colourAnimator.toAlarmGreen(statusView);
                }
                currentStatus = DISARMED;

                statusText.setTextColor(Color.parseColor("#FFFFFF"));

                enableButtons();
                statusText.setText("Disarmed");
                pullToRefresh.setRefreshing(false);

                Log.d("TCP", "Alarm is disarmed");


            } else if (values[0].contains("ARMED ***STAY***")) {
                isConnected = true;
                toastPresenter.cancelSendArmToast();

                if (currentStatus != ARMED_STAY) {
                    colourAnimator.toAlarmRed(statusView);
                }
                currentStatus = ARMED_STAY;

                statusText.setTextColor(Color.parseColor("#FFFFFF"));
                statusText.setText("Armed (Stay)");

                enableButtons();
                pullToRefresh.setRefreshing(false);

                Log.d("TCP", "Alarm is armed");


            } else if (values[0].contains("FAILED")) {
                pullToRefresh.setRefreshing(false);
                isConnected = false;

                disableButtons();

                dialogPresenter.showUnableToLoginDialog();

                nullTCPObjects();

            } else if (values[0].equals("Connection Reset")) {
                pullToRefresh.setRefreshing(false);
                isConnected = false;
                disableButtons();

                dialogPresenter.showConnectionResetDialog();
                nullTCPObjects();

            } else if (values[0].equals("Connection Timeout")) {
                pullToRefresh.setRefreshing(false);
                isConnected = false;
                disableButtons();

                dialogPresenter.showConnectionTimeoutDialog();
                nullTCPObjects();
            }
        }
    }
}