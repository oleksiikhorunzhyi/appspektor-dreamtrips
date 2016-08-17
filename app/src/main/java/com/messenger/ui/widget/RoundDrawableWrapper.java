package com.messenger.ui.widget;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;

public class RoundDrawableWrapper extends ShapeDrawable {
   private Drawable baseDrawable;

   private RoundDrawableWrapper(Builder builder) {
      super(builder.shape);
      baseDrawable = builder.drawable;

      Paint paint = getPaint();
      paint.setColor(builder.color);
   }

   @Override
   public void draw(Canvas canvas) {
      super.draw(canvas);
      baseDrawable.draw(canvas);
   }

   @Override
   public void setAlpha(int alpha) {
      super.setAlpha(alpha);
      baseDrawable.setAlpha(alpha);
   }

   @Override
   public void setColorFilter(ColorFilter colorFilter) {
      super.setColorFilter(colorFilter);
      baseDrawable.setColorFilter(colorFilter);
   }

   @Override
   public void setFilterBitmap(boolean filter) {
      baseDrawable.setFilterBitmap(filter);
   }

   @Override
   @TargetApi(Build.VERSION_CODES.M)
   public boolean isFilterBitmap() {
      return baseDrawable.isFilterBitmap();
   }

   @Override
   public void setTintMode(PorterDuff.Mode tintMode) {
      super.setTintMode(tintMode);
      baseDrawable.setTintMode(tintMode);
   }

   @Override
   public int getOpacity() {
      return baseDrawable.getOpacity();
   }

   @Override
   public void setBounds(int left, int top, int right, int bottom) {
      super.setBounds(left, top, right, bottom);
      baseDrawable.setBounds(getBounds());
   }

   public void setColor(int color) {
      getPaint().setColor(color);
   }

   public static final class Builder {
      private Drawable drawable;
      private int color;
      private Shape shape = new OvalShape();

      public Builder() {
      }

      public Builder color(int val) {
         color = val;
         return this;
      }

      public Builder drawable(Drawable drawable) {
         this.drawable = drawable;
         return this;
      }

      public Builder round() {
         Shape shape = new OvalShape();
         return this;
      }

      public Builder roundRect(int radius) {
         float[] radii = {radius, radius, radius, radius, radius, radius, radius, radius};
         this.shape = new RoundRectShape(radii, null, null);
         return this;
      }

      public RoundDrawableWrapper build() {
         return new RoundDrawableWrapper(this);
      }
   }
}
