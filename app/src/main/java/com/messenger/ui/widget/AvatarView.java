package com.messenger.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.R;

/**
 * Class for displaying online status indicator on the top of the rim of the rounded avatar.
 * If no online indicator is needed plain RoundedImageView can be used instead.
 */
public class AvatarView extends SimpleDraweeView {

    private static final float ONLINE_SIZE_FACTOR = 0.20f;

    private Drawable onlineIndicatorDrawable;
    private boolean isOnline;

    public AvatarView(Context context) {
        super(context);
    }

    public AvatarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        onlineIndicatorDrawable = ContextCompat.getDrawable(getContext(), R.drawable.circle_online_indicator);
        int onlineIndicatorSize = Math.round(getMeasuredWidth() * Math.abs(1f - getMeasuredWidth() % 1000 / 1000f) * ONLINE_SIZE_FACTOR);

        float avatarRadius = getMeasuredWidth() / 2;
        // Use following circle equation to calculate margins:
        // y = y0 + R * sin(alpha);
        // alpha = 45 degrees, sin 45% degrees = (sqrt(2)/2)
        double xyCoordinateOfOnlineIndicator = avatarRadius - (avatarRadius * Math.sqrt(2) / 2);
        // x and y coordinates are the same, so we can use the same margin
        int drawingStartCoordinate = (int) (Math.round(xyCoordinateOfOnlineIndicator - onlineIndicatorSize / 2));

        onlineIndicatorDrawable.setBounds(drawingStartCoordinate, drawingStartCoordinate,
                drawingStartCoordinate + onlineIndicatorSize,
                drawingStartCoordinate + onlineIndicatorSize);
    }

    public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isOnline) {
            onlineIndicatorDrawable.draw(canvas);
        }
    }
}
