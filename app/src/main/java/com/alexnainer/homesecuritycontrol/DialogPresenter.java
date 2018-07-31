package com.alexnainer.homesecuritycontrol;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

public class DialogPresenter {


    private AlertDialog.Builder unableToLoginDialog;
    private AlertDialog.Builder connectionResetDialog;
    private AlertDialog.Builder connectionTimeoutDialog;

    public DialogPresenter(Context context) {

        unableToLoginDialog = new AlertDialog.Builder(context);
        unableToLoginDialog.setTitle("Unable to Login");
        unableToLoginDialog.setMessage(context.getResources().getString(R.string.unable_to_login_message));
        unableToLoginDialog.setCancelable(true);

        unableToLoginDialog.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        unableToLoginDialog.create();


        connectionResetDialog = new AlertDialog.Builder(context);
        connectionResetDialog.setTitle("Connection Reset");
        connectionResetDialog.setMessage(context.getResources().getString(R.string.connection_reset_message));
        connectionResetDialog.setCancelable(true);

        connectionResetDialog.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        connectionResetDialog.create();


        connectionTimeoutDialog = new AlertDialog.Builder(context);
        connectionTimeoutDialog.setTitle("Connection Timeout");
        connectionTimeoutDialog.setMessage(context.getResources().getString(R.string.connection_timeout_message));
        connectionTimeoutDialog.setCancelable(true);

        connectionTimeoutDialog.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        connectionTimeoutDialog.create();
    }

    public void showUnableToLoginDialog() {
        unableToLoginDialog.show();
    }

    public void showConnectionResetDialog() {
        connectionResetDialog.show();
    }

    public void showConnectionTimeoutDialog() {
        connectionTimeoutDialog.show();
    }


}
