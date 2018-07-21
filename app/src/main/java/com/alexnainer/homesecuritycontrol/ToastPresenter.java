package com.alexnainer.homesecuritycontrol;

import android.content.Context;
import android.widget.Toast;

public class ToastPresenter {

    private CharSequence connectingText  = "Connecting...";
    private CharSequence sendArmText  = "Attempting to Arm...";
    private CharSequence sendDisarmText  = "Attempting to Disarm...";
    private CharSequence cannotConnectText  = "Cannot connect!";
    private int shortDuration = Toast.LENGTH_SHORT;
    private int LongDuration = Toast.LENGTH_LONG;
    private Toast connectingToast;
    private Toast successToast;
    private Toast sendArmToast;
    private Toast sendDisarmToast;
    private Toast cannotConnectToast;

    public ToastPresenter(Context context) {
        connectingToast = Toast.makeText(context, connectingText, shortDuration);
        successToast = Toast.makeText(context, "Success!", shortDuration);
        sendDisarmToast = Toast.makeText(context, sendDisarmText, LongDuration);
        sendArmToast = Toast.makeText(context, sendArmText, LongDuration);
        cannotConnectToast = Toast.makeText(context, cannotConnectText, LongDuration);
    }

    public void showSuccessToast(){
        successToast.show();
    }

    public void showSendDisarmToast(){
        sendDisarmToast.show();
    }

    public void showSendArmToast(){
        sendArmToast.show();
    }

    public void cancelSendDisarmToast(){
        sendDisarmToast.cancel();
    }

    public void cancelSendArmToast(){
        sendArmToast.cancel();
    }

    public void showCannotConnectToast(){
        cannotConnectToast.show();
    }



}
