package com.worldventures.dreamtrips.modules.tripsimages.view.custom;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;

import com.facebook.drawee.view.SimpleDraweeView;

import icepick.Icepick;
import icepick.State;


public class ScaleImageView extends SimpleDraweeView {

   String TAG = "ScaleImageView";
   private Context context;
   private float MAX_SCALE = 3f;
   private Matrix matrix;
   // display width height.
   private int width;
   private int height;
   private float scale;
   private float minScale;
   private float prevDistance;
   private boolean isScaling;
   private int prevMoveX;
   private int prevMoveY;
   private GestureDetector detector;
   private boolean scaleEnabled = true;

   private SingleTapListener singleTapListener;
   private DoubleTapListener doubleTapListener;

   @State int intrinsicWidth;
   @State int intrinsicHeight;

   public ScaleImageView(Context context, AttributeSet attr) {
      super(context, attr);
      this.context = context;
      initialize();
   }

   public ScaleImageView(Context context) {
      super(context);
      this.context = context;
      initialize();
   }

   @Override
   public Parcelable onSaveInstanceState() {
      return Icepick.saveInstanceState(this, super.onSaveInstanceState());
   }

   @Override
   public void onRestoreInstanceState(Parcelable state) {
      super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));
   }

   private void initialize() {
      ViewTreeObserver.OnGlobalLayoutListener listener = new ViewTreeObserver.OnGlobalLayoutListener() {
         @Override
         public void onGlobalLayout() {
            intrinsicWidth = ScaleImageView.this.getWidth();
            intrinsicHeight = ScaleImageView.this.getHeight();
            requestLayout();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
               getViewTreeObserver().removeGlobalOnLayoutListener(this);
            } else {
               getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
         }
      };
      getViewTreeObserver().addOnGlobalLayoutListener(listener);
      matrix = new Matrix();
      detector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
         @Override
         public boolean onDoubleTap(MotionEvent e) {
            if (doubleTapListener != null) doubleTapListener.onDoubleTap();
            if (!scaleEnabled) return true;
            //
            maxZoomTo((int) e.getX(), (int) e.getY());
            cutting();
            return true;
         }

         @Override
         public boolean onSingleTapConfirmed(MotionEvent e) {
            if (singleTapListener != null) singleTapListener.onTap();
            return true;
         }
      });

      setOnTouchListener((view, motionEvent) -> {
         view.getParent().requestDisallowInterceptTouchEvent(getScale() > 1.1);
         return false;
      });
   }

   @Override
   protected void onDraw(@NonNull Canvas canvas) {
      int saveCount = canvas.save();
      canvas.concat(matrix);
      super.onDraw(canvas);
      canvas.restoreToCount(saveCount);
   }

   @Override
   protected boolean setFrame(int l, int t, int r, int b) {
      width = r - l;
      height = b - t;

      matrix.reset();
      int r_norm = r - l;
      scale = (float) r_norm / (float) intrinsicWidth;

      int paddingHeight = 0;
      int paddingWidth = 0;
      // scaling vertical
      if (scale * intrinsicHeight > height) {
         scale = (float) height / (float) intrinsicHeight;
         matrix.postScale(scale, scale);
         paddingWidth = (r - width) / 2;
         paddingHeight = 0;
         // scaling horizontal
      } else {
         matrix.postScale(scale, scale);
         paddingHeight = (b - height) / 2;
         paddingWidth = 0;
      }
      matrix.postTranslate(paddingWidth, paddingHeight);

      invalidate();
      minScale = scale;
      zoomTo(scale, width / 2, height / 2);
      cutting();
      return super.setFrame(l, t, r, b);
   }

   protected float getValue(Matrix matrix, int whichValue) {
      float[] floats = new float[9];
      matrix.getValues(floats);
      return floats[whichValue];
   }

   protected float getScale() {
      return getValue(matrix, Matrix.MSCALE_X);
   }

   public float getTranslateX() {
      return getValue(matrix, Matrix.MTRANS_X);
   }

   protected float getTranslateY() {
      return getValue(matrix, Matrix.MTRANS_Y);
   }

   protected void maxZoomTo(int x, int y) {
      if (minScale != getScale() && (getScale() - minScale) > 0.1f) {
         // threshold 0.1f
         float scale = minScale / getScale();
         zoomTo(scale, x, y);
      } else {
         float scale = MAX_SCALE / getScale();
         zoomTo(scale, x, y);
      }
   }

   public void zoomTo(float scale, int x, int y) {
      if (getScale() * scale < minScale) {
         return;
      }
      if (scale >= 1 && getScale() * scale > MAX_SCALE) {
         return;
      }
      matrix.postScale(scale, scale);
      // move to center
      matrix.postTranslate(-(width * scale - width) / 2, -(height * scale - height) / 2);

      // move x and y distance
      matrix.postTranslate(-(x - (width / 2)) * scale, 0);
      matrix.postTranslate(0, -(y - (height / 2)) * scale);
      invalidate();
   }

   public void cutting() {
      int width = (int) (intrinsicWidth * getScale());
      int height = (int) (intrinsicHeight * getScale());
      if (getTranslateX() < -(width - this.width)) {
         matrix.postTranslate(-(getTranslateX() + width - this.width), 0);
      }
      if (getTranslateX() > 0) {
         matrix.postTranslate(-getTranslateX(), 0);
      }
      if (getTranslateY() < -(height - this.height)) {
         matrix.postTranslate(0, -(getTranslateY() + height - this.height));
      }
      if (getTranslateY() > 0) {
         matrix.postTranslate(0, -getTranslateY());
      }
      if (width < this.width) {
         matrix.postTranslate((this.width - width) / 2, 0);
      }
      if (height < this.height) {
         matrix.postTranslate(0, (this.height - height) / 2);
      }
      invalidate();
   }

   private float distance(float x0, float x1, float y0, float y1) {
      float x = x0 - x1;
      float y = y0 - y1;
      return (float) Math.sqrt(x * x + y * y);
   }

   private float dispDistance() {
      return (float) Math.sqrt(width * width + height * height);
   }

   @Override
   public boolean onTouchEvent(MotionEvent event) {
      if (detector.onTouchEvent(event)) {
         return true;
      }
      if (!scaleEnabled) return false;
      //
      int touchCount = event.getPointerCount();
      switch (event.getAction()) {
         case MotionEvent.ACTION_DOWN:
         case MotionEvent.ACTION_POINTER_1_DOWN:
         case MotionEvent.ACTION_POINTER_2_DOWN:
            if (touchCount >= 2) {
               float distance = distance(event.getX(0), event.getX(1), event.getY(0), event.getY(1));
               prevDistance = distance;
               isScaling = true;
            } else {
               prevMoveX = (int) event.getX();
               prevMoveY = (int) event.getY();
            }
         case MotionEvent.ACTION_MOVE:
            if (touchCount >= 2 && isScaling) {
               float dist = distance(event.getX(0), event.getX(1), event.getY(0), event.getY(1));
               float scale = (dist - prevDistance) / dispDistance();
               prevDistance = dist;
               scale += 1;
               scale = scale * scale;
               zoomTo(scale, width / 2, height / 2);
               cutting();
            } else if (!isScaling) {
               int distanceX = prevMoveX - (int) event.getX();
               int distanceY = prevMoveY - (int) event.getY();
               prevMoveX = (int) event.getX();
               prevMoveY = (int) event.getY();
               matrix.postTranslate(-distanceX, -distanceY);
               cutting();
            }
            break;
         case MotionEvent.ACTION_UP:
         case MotionEvent.ACTION_POINTER_UP:
         case MotionEvent.ACTION_POINTER_2_UP:
            if (event.getPointerCount() <= 1) {
               isScaling = false;
            }

            break;
      }
      return true;
   }

   @Override
   public void onConfigurationChanged(Configuration newConfig) {
      super.onConfigurationChanged(newConfig);
      initialize();
      requestLayout();
   }

   @Override
   public boolean dispatchTouchEvent(MotionEvent event) {
      super.dispatchTouchEvent(event);
      return true;
   }

   public void reset() {
      matrix.reset();
      invalidate();
   }

   public void setSingleTapListener(SingleTapListener singleTapListener) {
      this.singleTapListener = singleTapListener;
   }

   public void setDoubleTapListener(DoubleTapListener doubleTapListener) {
      this.doubleTapListener = doubleTapListener;
   }

   public void setScaleEnabled(boolean scaleEnabled) {
      this.scaleEnabled = scaleEnabled;
   }

   public interface SingleTapListener {
      void onTap();
   }

   public interface DoubleTapListener {
      void onDoubleTap();
   }
}