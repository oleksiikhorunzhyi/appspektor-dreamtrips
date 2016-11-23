package com.worldventures.dreamtrips.wallet.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;

public class BatteryView extends AppCompatImageView {

   public static final int BATTTER_LOW_THRESHOLD = 20;
   private final float rightLevelMargin;
   private final float leftLevelMargin;
   private final float topLevelMargin;
   private final float bottomLevelMargin;

   private Paint levelPaint = new Paint();
   private int level = 0;

   public BatteryView(Context context, AttributeSet attrs) {
      this(context, attrs, 0);
   }

   public BatteryView(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      //        setup margins
      rightLevelMargin = ViewUtils.pxFromDp(getContext(), 3f);
      leftLevelMargin = ViewUtils.pxFromDp(getContext(), 2f);
      topLevelMargin = ViewUtils.pxFromDp(getContext(), 2f);
      bottomLevelMargin = ViewUtils.pxFromDp(getContext(), 2f);
   }

   public void setLevel(int level) {
      this.level = level;
      invalidate();
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      setImageResource(R.drawable.ic_wallet_battery);
   }

   private int getColorPaintByLevel(int level) {
      if (level < BATTTER_LOW_THRESHOLD) {
         return getResources().getColor(R.color.wallet_battery_low_level);
      } else {
         return getResources().getColor(R.color.wallet_battery_high_level);
      }
   }

   @Override
   protected void onDraw(Canvas canvas) {
      super.onDraw(canvas);
      int primaryColor = getColorPaintByLevel(level);
      levelPaint.setColor(primaryColor);
      if(!isInEditMode()) setColorFilter(primaryColor, PorterDuff.Mode.MULTIPLY);

      drawBatteryLevel(canvas);
   }

   private void drawBatteryLevel(Canvas canvas) {
      int width = getWidth();
      int height = getHeight();
      float rightBorder = (width - rightLevelMargin) * (level / 100f);
      canvas.drawRect(leftLevelMargin, topLevelMargin, rightBorder, height - bottomLevelMargin, levelPaint);
   }
}
