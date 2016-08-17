package com.messenger.ui.widget.roundedcorners;

import android.graphics.Canvas;

interface RoundedCornersDelegate {

   void initialize(RoundedCornersLayout view, int radius);

   void dispatchDraw(Canvas canvas, Runnable dispatchSuper);
}
