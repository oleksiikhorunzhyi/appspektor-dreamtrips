package com.worldventures.dreamtrips.wallet.ui.dashboard.util.model;


import android.support.annotation.DrawableRes;

public class TransitionModel {
   private boolean defaultCard;
   private int left;
   private int top;
   private int width;
   private int height;
   private int overlap;
   private @DrawableRes int background;

   public TransitionModel(boolean defaultCard, int left, int top, int width, int height,
         int overlap, @DrawableRes int background) {
      this.defaultCard = defaultCard;
      this.left = left;
      this.top = top;
      this.width = width;
      this.height = height;
      this.overlap = overlap;
      this.background = background;
   }

   public boolean isDefaultCard() {
      return defaultCard;
   }

   public int getTop() {
      return top;
   }

   public int getLeft() {
      return left;
   }

   public int getWidth() {
      return width;
   }

   public int getHeight() {
      return height;
   }

   public int getOverlap() {
      return overlap;
   }

   public int getBackground() {
      return background;
   }
}
