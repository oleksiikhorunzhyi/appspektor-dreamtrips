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

   private static final int[] SCANNER_ALPHA = new int[]{0, 64, 128, 192, 255, 192, 128, 64};

   private Paint viewPaint;
   private Paint framingPaint;
   private Paint framingBorderPaint;
   private Paint laserPaint;

   private int framingLeft;
   private int framingTop;
   private int framingRight;
   private int framingBottom;
   private int borderWidth;
   private int scannerAlpha;

   private boolean enableLaserAnimation = true;

   public WalletBarCodeFinder(Context context, boolean enableLaserAnimation) {
      super(context);
      this.enableLaserAnimation = enableLaserAnimation;
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
      borderWidth = getResources().getDimensionPixelOffset(R.dimen.wallet_wizard_bar_code_border_stroke_width);
      framingBorderPaint = new Paint();
      framingBorderPaint.setColor(ContextCompat.getColor(getContext(), R.color.wallet_bar_code_scanner_border));
      framingBorderPaint.setStyle(Paint.Style.STROKE);
      framingBorderPaint.setStrokeWidth(borderWidth);

      laserPaint = new Paint();
      laserPaint.setColor(ContextCompat.getColor(getContext(), R.color.wallet_laser_line_color));
      laserPaint.setStyle(Paint.Style.STROKE);
      laserPaint.setStrokeWidth(getResources().getDimensionPixelOffset(R.dimen.wallet_wizard_bar_code_laser_line_border_width));

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
      canvas.drawRect(new RectF(getFramingRect()), framingPaint);
      canvas.drawRect(new RectF(getFramingRect()), framingBorderPaint);
   }

   @Override
   public void drawLaser(Canvas canvas) {
      Rect framingRect = getFramingRect();
      if (enableLaserAnimation) {
         laserPaint.setAlpha(SCANNER_ALPHA[this.scannerAlpha]);
         scannerAlpha = (this.scannerAlpha + 1) % SCANNER_ALPHA.length;
      }
      int middle = framingRect.height() / 2 + framingRect.top;
      canvas.drawLine((float)(framingRect.left + 4), middle,
            (float)(framingRect.right - 4), middle, laserPaint);
      postInvalidateDelayed(80L, framingRect.left, framingRect.top - borderWidth,
            framingRect.right, framingRect.bottom + borderWidth);
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