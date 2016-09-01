package com.worldventures.dreamtrips.wallet.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;

import com.worldventures.dreamtrips.R;

import me.dm7.barcodescanner.core.ViewFinderView;

public class WalletBarCodeFinder extends ViewFinderView {

   private Paint viewPaint;
   private Paint framingPaint;
   private Paint framingBorderPaint;

   private int framingBorderRadius;

   private int framingLeft;
   private int framingTop;
   private int framingRight;
   private int framingBottom;

   public WalletBarCodeFinder(Context context) {
      super(context);
      init();
   }

   private void init() {
      viewPaint = new Paint();
      viewPaint.setColor(ContextCompat.getColor(getContext(), R.color.wallet_color_background_with_alpha));
      viewPaint.setStyle(Paint.Style.FILL);

      //clear rounded rect scan zone
      framingPaint = new Paint();
      framingPaint.setStyle(Paint.Style.FILL_AND_STROKE);
      framingPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

      //draw border for scan zone
      framingBorderPaint = new Paint();
      framingBorderPaint.setColor(ContextCompat.getColor(getContext(), R.color.wallet_bar_code_scanner_border));
      framingBorderPaint.setStyle(Paint.Style.STROKE);
      framingBorderPaint.setStrokeWidth(getResources().getDimensionPixelOffset(R.dimen.wallet_wizard_bar_code_border_stroke_width));

      framingBorderRadius = getResources().getDimensionPixelOffset(R.dimen.wallet_wizard_bar_code_border_radius);

      framingLeft = getResources().getDimensionPixelOffset(R.dimen.wallet_wizard_bar_code_view_left);
      framingTop = getResources().getDimensionPixelOffset(R.dimen.wallet_wizard_bar_code_view_top);
      framingRight = getResources().getDimensionPixelOffset(R.dimen.wallet_wizard_bar_code_view_right);
      framingBottom = getResources().getDimensionPixelOffset(R.dimen.wallet_wizard_bar_code_view_bottom);
   }

   @Override
   public void drawViewFinderMask(Canvas canvas) {
      canvas.drawRect(new Rect(getLeft(), getTop(), getRight(), getBottom()), viewPaint);
   }

   @Override
   public void drawViewFinderBorder(Canvas canvas) {
      canvas.drawRoundRect(new RectF(getFramingRect()), framingBorderRadius, framingBorderRadius, framingPaint);
      canvas.drawRoundRect(new RectF(getFramingRect()), framingBorderRadius, framingBorderRadius, framingBorderPaint);
   }

   @Override
   public void drawLaser(Canvas canvas) {
      //no laser line
   }

   @Override
   public synchronized void updateFramingRect() {
      super.updateFramingRect();
      getFramingRect().left = framingLeft;
      getFramingRect().top = framingTop;
      getFramingRect().right = getRight() - framingRight;
      getFramingRect().bottom = getBottom() - framingBottom;
   }
}