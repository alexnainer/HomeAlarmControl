package com.alexnainer.homealarmcontrol;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class FirebaseManager {

    private FirebaseAnalytics mFirebaseAnalytics;

    FirebaseManager(Context context) {

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public void eventRefreshButtonClick() {
        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent("refresh_button_clicked", bundle);
    }

    public void eventAutoConnect() {
        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent("auto_connect", bundle);
    }

    public void eventRefreshPulled() {
        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent("refresh_pulled", bundle);
    }

    public void eventDisarmButtonClick() {
        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent("disarm_button_clicked", bundle);
    }

    public void eventArmStayButtonClick() {
        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent("arm_stay_button_clicked", bundle);
    }

    public void eventArmAwayButtonClick() {
        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent("arm_away_button_clicked", bundle);
    }

    public void eventSuccessfulConnection() {
        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent("successful_connection", bundle);
    }

    public void eventConnectionFail() {
        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent("connection_failed", bundle);
    }

    public void eventConnectionReset() {
        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent("connection_reset", bundle);
    }

    public void eventConnectionTimeout() {
        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent("connection_timeout", bundle);
    }

    public void eventNoIPAddress() {
        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent("no_ip_address", bundle);
    }

    public void propertyShowArmAwayButton(boolean isShowing) {
        mFirebaseAnalytics.setUserProperty("show_arm_away_button", isShowing + "");
    }

    public void propertyAutoConnect(boolean isAutoConnect) {
        mFirebaseAnalytics.setUserProperty("auto_connect_enabled", isAutoConnect + "");
    }
}
