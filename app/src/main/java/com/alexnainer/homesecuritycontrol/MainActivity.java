package com.alexnainer.homesecuritycontrol;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public TCPClient tcpClient;
    public CommandSender commandSender;

    final int DISARMED = 0;
    final int ARMED_STAY = 1;
    final int ARMED_AWAY = 2;

    int alarmStatus;

    boolean attemptingToConnect = false;

    
    //Toasts
    public Context context;
    CharSequence connectingText  = "Connecting...";
    CharSequence sendArmText  = "Attempting to Arm...";
    CharSequence sendDisarmText  = "Attempting to Disarm...";
    CharSequence cannotConnectText  = "Cannot connect!";
    int shortDuration = Toast.LENGTH_SHORT;
    int LongDuration = Toast.LENGTH_LONG;
    Toast connectingToast;
    Toast successToast;
    Toast sendArmToast;
    Toast sendDisarmToast;
    Toast cannotConnectToast;

    Toolbar toolbar;

    View statusView;
    TextView statusText;

    SwipeRefreshLayout pullToRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        //Toasts
        context = getApplicationContext();
        connectingToast = Toast.makeText(context, connectingText, shortDuration);
        successToast = Toast.makeText(context, "Success!", shortDuration);
        sendDisarmToast = Toast.makeText(context, sendDisarmText, LongDuration);
        sendArmToast = Toast.makeText(context, sendArmText, LongDuration);
        cannotConnectToast = Toast.makeText(context, cannotConnectText, LongDuration);


        pullToRefresh = findViewById(R.id.pullToRefresh);

        commandSender = new CommandSender();


        statusView = findViewById(R.id.statusView);
        statusText = findViewById(R.id.statusText);
        statusText.setTextColor(Color.parseColor("#000000"));


        CardView armButton = findViewById(R.id.armButton);
        armButton.setOnClickListener(this);

        CardView disarmButton = findViewById(R.id.disarmButton);
        disarmButton.setOnClickListener(this);
        statusText.setText("Not Connected");

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                attemptingToConnect = true;
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                if (attemptingToConnect) {
                                    pullToRefresh.setRefreshing(false);
                                    cannotConnectToast.show();
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
                        3000);




                new connectTask().execute("");

                Log.d("TCP", "attempting to connect...");



            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.settingsButton) {
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
                sendArmToast.show();
                commandSender.sendArm(tcpClient);
                break;

            case R.id.disarmButton:
                Log.d("TCP", "disarm onClick");
                sendDisarmToast.show();
                commandSender.sendDisarm(tcpClient);
                break;

            default:
                break;

        }


    }

    public class connectTask extends AsyncTask<String, String, TCPClient> {

        @Override
        protected TCPClient doInBackground(String... message) {

            //we create a TCPClient object and
            tcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    publishProgress(message);

                }
            });
            tcpClient.run();

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            Log.d("TCP", "Response from Server: " + values[0]);

            if (values[0].equals("Login:")) {
                attemptingToConnect = false;
                commandSender.sendLogin(tcpClient, "r21n7X");
            } else if (values[0].equals("OK")) {
                statusText.setText("Getting Status...");
                successToast.show();

            } else if (values[0].contains("****DISARMED****")) {
                sendDisarmToast.cancel();
                attemptingToConnect = false;
                alarmStatus = DISARMED;


                statusText.setTextColor(Color.parseColor("#FFFFFF"));
                statusView.setBackgroundColor(Color.parseColor("#388E3C"));
                statusText.setText("Disarmed");
                pullToRefresh.setRefreshing(false);
                Log.d("TCP", "Alarm is disarmed");


            } else if (values[0].contains("ARMED ***STAY***")) {
                sendArmToast.cancel();
                attemptingToConnect = false;
                alarmStatus = ARMED_STAY;

                statusText.setTextColor(Color.parseColor("#FFFFFF"));
                statusView.setBackgroundColor(Color.parseColor("#d32f2f"));
                statusText.setText("Armed (Stay)");
                pullToRefresh.setRefreshing(false);
                Log.d("TCP", "Alarm is armed");


            }
        }
    }


}