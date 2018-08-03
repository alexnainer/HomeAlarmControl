package com.alexnainer.homesecuritycontrol;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

public class SnackbarPresenter {

    private Snackbar armedAwaySnackbar;
    private Snackbar connectingSnackbar;

    private CancelConnectCallback cancelConnectCallback;

    public SnackbarPresenter(View rootView, final CancelConnectCallback mCancelConnectCallback ) {

        cancelConnectCallback = mCancelConnectCallback;

        armedAwaySnackbar = Snackbar.make(rootView, R.string.alarm_away_message, Snackbar.LENGTH_INDEFINITE);

        connectingSnackbar = Snackbar.make(rootView, R.string.connecting_message, Snackbar.LENGTH_INDEFINITE)
                .setAction("Cancel", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cancelConnectCallback.onConnectCancel();
                    }
        });;

    }

    public void showArmedAwaySnackbar() {
        armedAwaySnackbar.show();
    }

    public void dismissArmedAwaySnackbar() {
        armedAwaySnackbar.dismiss();
    }

    public void showConnectingSnackbar() {
        connectingSnackbar.show();
    }

    public void dismissConnectingSnackbar() {
        connectingSnackbar.dismiss();
    }


}
