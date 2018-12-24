package com.alexnainer.homealarmcontrol;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.View;

public class ColourAnimator {

    private Context context;
    private int animationDuration = 300;

    ColourAnimator(Context context) {
        this.context = context;
    }


    public void toAlarmRed(final View view) {

        int currentColor = Color.TRANSPARENT;
        Drawable background = view.getBackground();
        if (background instanceof ColorDrawable) {
            currentColor = ((ColorDrawable) background).getColor();
        }

        int alarmRed = ContextCompat.getColor(context, R.color.redArmed);

        ValueAnimator colorAnimation = new ValueAnimator();
        colorAnimation.setIntValues(currentColor, alarmRed);
        colorAnimation.setEvaluator(new ArgbEvaluator());
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.setBackgroundColor((Integer)valueAnimator.getAnimatedValue());
            }
        });

        colorAnimation.setDuration(animationDuration);
        colorAnimation.start();

    }

    public void toAlarmOrange(final View view) {

        int currentColor = Color.TRANSPARENT;
        Drawable background = view.getBackground();
        if (background instanceof ColorDrawable) {
            currentColor = ((ColorDrawable) background).getColor();
        }

        int alarmOrange = ContextCompat.getColor(context, R.color.orangeArmedAway);

        ValueAnimator colorAnimation = new ValueAnimator();
        colorAnimation.setIntValues(currentColor, alarmOrange);
        colorAnimation.setEvaluator(new ArgbEvaluator());
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.setBackgroundColor((Integer)valueAnimator.getAnimatedValue());
            }
        });

        colorAnimation.setDuration(animationDuration);
        colorAnimation.start();

    }


    public void toAlarmGreen(final View view) {

        int currentColor = Color.TRANSPARENT;
        Drawable background = view.getBackground();
        if (background instanceof ColorDrawable) {
            currentColor = ((ColorDrawable) background).getColor();
        }

        int alarmGreen = ContextCompat.getColor(context, R.color.greenDisarmed);

        ValueAnimator colorAnimation = new ValueAnimator();
        colorAnimation.setIntValues(currentColor, alarmGreen);
        colorAnimation.setEvaluator(new ArgbEvaluator());
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.setBackgroundColor((Integer)valueAnimator.getAnimatedValue());
            }
        });

        colorAnimation.setDuration(animationDuration);
        colorAnimation.start();

    }

    public void toFaultGrey(final View view) {

        int currentColor = Color.TRANSPARENT;
        Drawable background = view.getBackground();
        if (background instanceof ColorDrawable) {
            currentColor = ((ColorDrawable) background).getColor();
        }

        int faultGrey = ContextCompat.getColor(context, R.color.grey_700);

        ValueAnimator colorAnimation = new ValueAnimator();
        colorAnimation.setIntValues(currentColor, faultGrey);
        colorAnimation.setEvaluator(new ArgbEvaluator());
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.setBackgroundColor((Integer)valueAnimator.getAnimatedValue());
            }
        });

        colorAnimation.setDuration(animationDuration);
        colorAnimation.start();

    }

}
