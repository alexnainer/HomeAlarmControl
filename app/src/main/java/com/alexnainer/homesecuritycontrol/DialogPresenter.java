package com.alexnainer.homesecuritycontrol;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

public class DialogPresenter {


    private AlertDialog.Builder unableToLoginDialog;
    private AlertDialog.Builder unableToConnectDialog;

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

        unableToConnectDialog = new AlertDialog.Builder(context);
        unableToConnectDialog.setTitle("Unable to Connect");
        unableToConnectDialog.setMessage(context.getResources().getString(R.string.unable_to_connect_message));
        unableToConnectDialog.setCancelable(true);

        unableToConnectDialog.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });


        unableToConnectDialog.create();
    }

    public void showUnableToLoginDialog() {
        unableToLoginDialog.show();
    }

    public void showUnableToConnectDialog() {
        unableToConnectDialog.show();
    }


}
