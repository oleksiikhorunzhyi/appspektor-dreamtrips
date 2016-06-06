package com.messenger.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.percent.PercentFrameLayout;
import android.util.AttributeSet;

public class RoundBackgroundPercentFrameLayout extends PercentFrameLayout {

    private Paint paint;

    public RoundBackgroundPercentFrameLayout(Context context) {
        super(context);
        init();
    }

    public RoundBackgroundPercentFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
    }

    public void setRoundBackgroundColor(int color) {
        paint.setColor(color);
        invalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int radius = getMeasuredHeight() / 2;
        canvas.drawCircle(radius, radius, radius, paint);
        super.dispatchDraw(canvas);
    }
}
