package com.alexnainer.homealarmcontrol;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.widget.TextView;

public class CardButton {

    private CardView cardView;
    private TextView textView;
    private SharedPreferences prefs;
    Context context;


    public CardButton(CardView cardView, TextView textView, Context context) {
        this.cardView = cardView;
        this.textView = textView;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

    }
    public void setEnabled() {

        cardView.setEnabled(true);
        cardView.setElevation(7);

        if (isDarkTheme()){
            cardView.setBackgroundColor(ContextCompat.getColor(context, R.color.grey_800));
            textView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        } else {
            cardView.setBackgroundColor(ContextCompat.getColor(context, R.color.cardEnabledLight));
            textView.setBackgroundColor(Color.BLACK);
        }
    }

    public void setDisabled() {

        cardView.setEnabled(false);
        cardView.setElevation(0);

        if (isDarkTheme()){
            cardView.setBackgroundColor(ContextCompat.getColor(context, R.color.grey_800));
            textView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        } else {
            cardView.setBackgroundColor(ContextCompat.getColor(context, R.color.cardDisabledLight));
            textView.setBackgroundColor(Color.BLACK);
        }
    }

    private boolean isDarkTheme() {
        if (prefs.getString("key_theme", "Light").equals("Dark")) {
            return true;
        } else {
            return false;
        }
    }


}
