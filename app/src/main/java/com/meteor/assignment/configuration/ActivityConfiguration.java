package com.meteor.assignment.configuration;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

public class ActivityConfiguration {
    public Drawable windowBackground;

    private static ActivityConfiguration instance = new ActivityConfiguration();

    private ActivityConfiguration(){
        windowBackground= new ColorDrawable(Color.WHITE);
    }

    public static ActivityConfiguration getInstance() {
        return instance;
    }

}
