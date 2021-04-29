package com.alexnainer.homealarmcontrol;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.view.View;

public class AlarmFragment extends Fragment {

    private TCPClient tcpClient;
    private MainActivity.TCPNetworkingTask tcpNetworking;
    private CommandSender commandSender;
    private ColourAnimator colourAnimator;
    private DialogPresenter dialogPresenter;
    private ToastPresenter toastPresenter;
    private SnackbarPresenter snackbarPresenter;

    CardView disarmButton;
    CardView armStayButton;
    CardView armAwayButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    public void setup(final Context context, View rootView) {
        colourAnimator = new ColourAnimator(context);
        commandSender = new CommandSender(context);
        dialogPresenter = new DialogPresenter(context);
        toastPresenter = new ToastPresenter(context);
        snackbarPresenter = new SnackbarPresenter(rootView, (CancelConnectCallback) context);
    }

//    public AlarmView(final Context context, View rootView) {
//        colourAnimator = new ColourAnimator(context);
//        commandSender = new CommandSender(context);
//        dialogPresenter = new DialogPresenter(context);
//        toastPresenter = new ToastPresenter(context);
//        snackbarPresenter = new SnackbarPresenter(rootView, (CancelConnectCallback) context);
//    }
}
