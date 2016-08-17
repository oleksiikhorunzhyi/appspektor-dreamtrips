package com.messenger.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.R;

import timber.log.Timber;

/**
 * Class for displaying online status indicator on the top of the rim of the rounded avatar.
 * If no online indicator is needed plain RoundedImageView can be used instead.
 */
public class AvatarView extends SimpleDraweeView {

   private static final float ONLINE_INDICATOR_SIZE_DEFAULT_DP = 8;

   private Drawable onlineIndicatorDrawable;
   private int onlineIndicatorSize;
   private boolean isOnline;

   public AvatarView(Context context) {
      super(context);
      init(null);
   }

   public AvatarView(Context context, AttributeSet attrs) {
      super(context, attrs);
      init(attrs);
   }

   private void init(AttributeSet attrs) {
      if (attrs == null) {
         return;
      }
      TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.AvatarView, 0, 0);
      try {
         if (!a.hasValue(R.styleable.AvatarView_av_online_indicator_size)) {
            return;
         }
         onlineIndicatorSize = a.getDimensionPixelSize(R.styleable.AvatarView_av_online_indicator_size, 0);
      } catch (Exception e) {
         Timber.e(e, "");
      } finally {
         a.recycle();
      }
   }

   @Override
   protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
      super.onMeasure(widthMeasureSpec, heightMeasureSpec);
      if (onlineIndicatorSize == 0) {
         onlineIndicatorSize = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ONLINE_INDICATOR_SIZE_DEFAULT_DP, getResources()
               .getDisplayMetrics()));
      }
      onlineIndicatorDrawable = ContextCompat.getDrawable(getContext(), R.drawable.circle_online_indicator);

      float avatarRadius = getMeasuredWidth() / 2;
      // Use following circle equation to calculate margins:
      // y = y0 + R * sin(alpha);
      // alpha = 45 degrees, sin 45% degrees = (sqrt(2)/2)
      double xyCoordinateOfOnlineIndicator = avatarRadius - (avatarRadius * Math.sqrt(2) / 2);
      // x and y coordinates are the same, so we can use the same margin
      int drawingStartCoordinate = (int) (Math.round(xyCoordinateOfOnlineIndicator - onlineIndicatorSize / 2));

      onlineIndicatorDrawable.setBounds(drawingStartCoordinate, drawingStartCoordinate, drawingStartCoordinate + onlineIndicatorSize, drawingStartCoordinate + onlineIndicatorSize);
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
