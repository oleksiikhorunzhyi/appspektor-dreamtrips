package com.messenger.ui.widget.roundedcorners;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;

final class RoundedCornersDelegateSupport implements RoundedCornersDelegate {

   private RoundedCornersLayout roundedCornersLayout;
   private int radius;

   @Override
   public void initialize(RoundedCornersLayout view, int radius) {
      this.radius = radius;
      this.roundedCornersLayout = view;
      view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
   }

   @Override
   public void dispatchDraw(Canvas canvas, Runnable dispatchDraw) {
      Path path = new Path();
      int count = canvas.save();

      path.addRoundRect(new RectF(roundedCornersLayout.getBoundsRect()), radius, radius, Path.Direction.CCW);

      canvas.clipPath(path);

      dispatchDraw.run();
      canvas.restoreToCount(count);
   }
}
