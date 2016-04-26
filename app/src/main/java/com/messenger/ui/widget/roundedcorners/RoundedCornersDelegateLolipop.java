package com.messenger.ui.widget.roundedcorners;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.view.View;
import android.view.ViewOutlineProvider;

final class RoundedCornersDelegateLolipop implements RoundedCornersDelegate {

    @SuppressLint("NewApi")
    @Override
    public void initialize(RoundedCornersLayout roundedCornersLayout, int radius) {
        roundedCornersLayout.setClipToOutline(true);
        roundedCornersLayout.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(roundedCornersLayout.getBoundsRect(), radius);
            }
        });
    }

    @Override
    public void dispatchDraw(Canvas canvas, Runnable dispatchSuper) {
        dispatchSuper.run();
    }
}
