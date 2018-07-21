package com.alexnainer.homesecuritycontrol;

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

        colorAnimation.setDuration(300);
        colorAnimation.start();

    }

    public void toAlarmGreen(final View view) {

        int currentColor = Color.TRANSPARENT;
        Drawable background = view.getBackground();
        if (background instanceof ColorDrawable) {
            currentColor = ((ColorDrawable) background).getColor();
        }

        int alarmRed = ContextCompat.getColor(context, R.color.greenDisarmed);

        ValueAnimator colorAnimation = new ValueAnimator();
        colorAnimation.setIntValues(currentColor, alarmRed);
        colorAnimation.setEvaluator(new ArgbEvaluator());
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.setBackgroundColor((Integer)valueAnimator.getAnimatedValue());
            }
        });

        colorAnimation.setDuration(300);
        colorAnimation.start();

    }

}
