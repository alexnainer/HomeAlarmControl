package com.alexnainer.homesecuritycontrol;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CancelConnectCallback  {

    private TCPClient tcpClient;
    private TCPNetworkingTask tcpNetworking;
    private CommandSender commandSender;
    private ColourAnimator colourAnimator;
    private DialogPresenter dialogPresenter;
    private ToastPresenter toastPresenter;
    private SnackbarPresenter snackbarPresenter;
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

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < 23) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.grey_700));
            getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(this, R.color.background_light));
        }


        context = getApplicationContext();

        pullToRefresh = findViewById(R.id.pullToRefresh);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        statusView = findViewById(R.id.statusView);
        statusText = findViewById(R.id.statusText);

        disarmButton = findViewById(R.id.disarmButton);
        disarmButton.setOnClickListener(this);

        armStayButton = findViewById(R.id.armStayButton);
        armStayButton.setOnClickListener(this);

        armAwayButton = findViewById(R.id.armAwayButton);
        armAwayButton.setOnClickListener(this);
        armAwayText = findViewById(R.id.armAwayText);

        dividerDisarmArm = findViewById(R.id.dividerBetweenArmDisarm);
        dividerArmStayAway = findViewById(R.id.dividerBetweenArmStayAway);


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

        boolean firstLaunch = prefs.getBoolean("key_first_launch", true);

        if (firstLaunch) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            prefs.edit().putBoolean("key_first_launch", false).commit();
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }


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

        colourAnimator = new ColourAnimator(this);
        commandSender = new CommandSender(this);
        dialogPresenter = new DialogPresenter(this);
        toastPresenter = new ToastPresenter(this);
        snackbarPresenter = new SnackbarPresenter(findViewById(android.R.id.content), this);

        setDisconnected();

        if(isAutoConnect && !didLaunchSettings){
            pullToRefresh.setRefreshing(true);
            attemptToConnect();
        }
        didLaunchSettings = false;
        currentStatus = DISCONNECTED;

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

        snackbarPresenter.dismissConnectingSnackbar();
        snackbarPresenter.dismissArmedAwaySnackbar();

        pullToRefresh.setRefreshing(false);
        commandSender = null;
        dialogPresenter = null;
        colourAnimator = null;
        snackbarPresenter = null;

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
            snackbarPresenter.showConnectingSnackbar();
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

    @Override
    public void onConnectCancel() {
        setDisconnected();
    }

    private void setDisconnected() {

        nullTCPObjects();

        snackbarPresenter.dismissConnectingSnackbar();
        statusView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        statusText.setText("Not Connected");
        statusText.setTextColor(Color.parseColor("#000000"));

        disableButtons();

        pullToRefresh.setRefreshing(false);
        isConnected = false;
        currentStatus = DISCONNECTED;
    }

    private void disableButtons() {

        armStayButton.setClickable(false);
        armAwayButton.setClickable(false);
        disarmButton.setClickable(false);

        armStayButton.setBackgroundColor(ContextCompat.getColor(context, R.color.cardDisabledLight));
        armAwayButton.setBackgroundColor(ContextCompat.getColor(context, R.color.cardDisabledLight));
        disarmButton.setBackgroundColor(ContextCompat.getColor(context, R.color.cardDisabledLight));

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

        armStayButton.setBackgroundColor(ContextCompat.getColor(context, R.color.cardEnabledLight));
        armAwayButton.setBackgroundColor(ContextCompat.getColor(context, R.color.cardEnabledLight));
        disarmButton.setBackgroundColor(ContextCompat.getColor(context, R.color.cardEnabledLight));

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
            startActivity(new Intent(MainActivity.this, SettingsPrefActivity.class));
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


            case R.id.disarmButton:
                Log.d("TCP", "disarm onClick");
                toastPresenter.showSendDisarmToast();
                commandSender.sendDisarm(tcpClient);
                pullToRefresh.setRefreshing(true);
                disableButtons();
                break;

            case R.id.armStayButton:
                Log.d("TCP", "arm stay onClick");
                toastPresenter.showSendArmStayToast();
                commandSender.sendArmStay(tcpClient);
                pullToRefresh.setRefreshing(true);
                disableButtons();
                break;

            case R.id.armAwayButton:
                Log.d("TCP", "arm away onClick");
                toastPresenter.showSendArmAwayToast();
                commandSender.sendArmAway(tcpClient);
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


            } else if (values[0].contains("****DISARMED****")) {
                isConnected = true;
                toastPresenter.cancelSendDisarmToast();
                snackbarPresenter.dismissConnectingSnackbar();
                snackbarPresenter.dismissArmedAwaySnackbar();

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
                toastPresenter.cancelSendArmStayToast();
                snackbarPresenter.dismissConnectingSnackbar();;
                snackbarPresenter.dismissArmedAwaySnackbar();


                if (currentStatus == ARMED_AWAY) {
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    // Vibrate for 500 milliseconds
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(300,VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        v.vibrate(300);
                    }
                }

                if (currentStatus != ARMED_STAY) {
                    colourAnimator.toAlarmRed(statusView);
                }
                currentStatus = ARMED_STAY;

                statusText.setTextColor(Color.parseColor("#FFFFFF"));
                statusText.setText("Armed");

                enableButtons();
                pullToRefresh.setRefreshing(false);

                Log.d("TCP", "Alarm is armed");


            } else if (values[0].contains("ARMED ***AWAY***You may exit now")) {
                isConnected = true;
                toastPresenter.cancelSendArmAwayToast();
                snackbarPresenter.dismissConnectingSnackbar();;
                snackbarPresenter.showArmedAwaySnackbar();


                if (currentStatus != ARMED_AWAY) {
                    colourAnimator.toAlarmOrange(statusView);
                }
                currentStatus = ARMED_AWAY;

                statusText.setTextColor(Color.parseColor("#FFFFFF"));
                statusText.setText("Armed (AWAY)");

                pullToRefresh.setRefreshing(false);

            } else if (values[0].contains("FAILED")) {
                pullToRefresh.setRefreshing(false);
                isConnected = false;
                snackbarPresenter.dismissConnectingSnackbar();

                disableButtons();

                dialogPresenter.showUnableToLoginDialog();

                nullTCPObjects();

            } else if (values[0].equals("Connection Reset")) {
                pullToRefresh.setRefreshing(false);
                isConnected = false;
                snackbarPresenter.dismissConnectingSnackbar();
                disableButtons();

                dialogPresenter.showConnectionResetDialog();
                nullTCPObjects();

            } else if (values[0].equals("Connection Timeout")) {
                pullToRefresh.setRefreshing(false);
                isConnected = false;
                snackbarPresenter.dismissConnectingSnackbar();
                disableButtons();

                dialogPresenter.showConnectionTimeoutDialog();
                nullTCPObjects();
            }
        }
    }
}