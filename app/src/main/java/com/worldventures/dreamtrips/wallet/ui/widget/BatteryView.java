package com.worldventures.dreamtrips.wallet.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;

public class BatteryView extends AppCompatImageView {
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
      rightLevelMargin = ViewUtils.pxFromDp(getContext(), 5);
      leftLevelMargin = ViewUtils.pxFromDp(getContext(), 3);
      topLevelMargin = ViewUtils.pxFromDp(getContext(), 3);
      bottomLevelMargin = ViewUtils.pxFromDp(getContext(), 3);
   }

   public void setLevel(int level) {
      this.level = level;
      invalidate();
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      int primaryColor = ContextCompat.getColor(getContext(), R.color.wallet_color_primary);
      levelPaint.setColor(primaryColor);
      setImageResource(R.drawable.ic_battery);
   }

   @Override
   protected void onDraw(Canvas canvas) {
      super.onDraw(canvas);
      drawBatteryLevel(canvas);
   }

   private void drawBatteryLevel(Canvas canvas) {
      Rect batteryBounds = getDrawable().getBounds();
      float rightBorder = (batteryBounds.right - rightLevelMargin) * (level / 100f);
      canvas.drawRect(batteryBounds.left + leftLevelMargin, batteryBounds.top + topLevelMargin, rightBorder, batteryBounds.bottom - bottomLevelMargin, levelPaint);
   }
}
