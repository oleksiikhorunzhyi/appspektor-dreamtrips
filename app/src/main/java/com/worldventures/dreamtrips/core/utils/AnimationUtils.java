package com.worldventures.dreamtrips.core.utils;

import android.os.Build;
import android.view.View;

public class AnimationUtils {
    private AnimationUtils() {
    }

    public static void hideInTopEdge(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.animate().translationY(-view.getHeight()).setDuration(200).withLayer();
        } else {
            view.animate().translationY(-view.getHeight()).setDuration(200);
        }
    }

    public static void appearFromTopEdge(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.animate().translationY(0).setDuration(200).withLayer();
        } else {
            view.animate().translationY(0).setDuration(200);
        }
    }

    public static void hideInBottomEdge(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.animate().translationY(view.getHeight()).setDuration(200).withLayer();
        } else {
            view.animate().translationY(view.getHeight()).setDuration(200);
        }
    }

    public static void appearFromBottomEdge(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.animate().translationY(0).setDuration(200).withLayer();
        } else {
            view.animate().translationY(0).setDuration(200);
        }
    }
}