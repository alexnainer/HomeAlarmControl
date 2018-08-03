package com.alexnainer.homesecuritycontrol;

import android.content.Context;
import android.widget.Toast;

public class ToastPresenter {

    private CharSequence connectingText  = "Connecting...";
    private CharSequence sendArmStayText = "Attempting to Arm (Stay)...";
    private CharSequence sendArmAwayText = "Attempting to Arm (Away)...";
    private CharSequence sendDisarmText  = "Attempting to Disarm...";
    private CharSequence cannotConnectText  = "Cannot connect!";
    private int shortDuration = Toast.LENGTH_SHORT;
    private int LongDuration = Toast.LENGTH_LONG;
    private Toast connectingToast;
    private Toast successToast;
    private Toast sendArmStayToast;
    private Toast sendArmAwayToast;
    private Toast sendDisarmToast;
    private Toast cannotConnectToast;

    public ToastPresenter(Context context) {
        connectingToast = Toast.makeText(context, connectingText, shortDuration);
        successToast = Toast.makeText(context, "Success!", shortDuration);
        sendDisarmToast = Toast.makeText(context, sendDisarmText, LongDuration);
        sendArmStayToast = Toast.makeText(context, sendArmStayText, LongDuration);
        sendArmAwayToast = Toast.makeText(context, sendArmAwayText, LongDuration);
        cannotConnectToast = Toast.makeText(context, cannotConnectText, LongDuration);
    }

    public void showSuccessToast(){
        successToast.show();
    }

    public void showSendDisarmToast(){
        sendDisarmToast.show();
    }

    public void showSendArmStayToast(){
        sendArmStayToast.show();
    }

    public void showSendArmAwayToast(){
        sendArmAwayToast.show();
    }

    public void cancelSendDisarmToast(){
        sendDisarmToast.cancel();
    }

    public void cancelSendArmStayToast(){
        sendArmStayToast.cancel();
    }

    public void cancelSendArmAwayToast(){
        sendArmAwayToast.cancel();
    }




}
