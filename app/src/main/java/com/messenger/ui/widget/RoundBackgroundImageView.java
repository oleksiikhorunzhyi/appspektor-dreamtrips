package com.messenger.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundBackgroundImageView extends ImageView {

    private Paint paint;

    public RoundBackgroundImageView(Context context) {
        super(context);
        init();
    }

    public RoundBackgroundImageView(Context context, AttributeSet attrs) {
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
    protected void onDraw(Canvas canvas) {
        int radius = getMeasuredHeight() / 2;
        canvas.drawCircle(radius, radius, radius, paint);
        super.onDraw(canvas);
    }
}
