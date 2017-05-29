package com.worldventures.dreamtrips.wallet.ui.dashboard.util.model;


public class TransitionModel {
   private int left;
   private int top;
   private int width;
   private int height;
   private int overlap;
   private boolean background;

   public TransitionModel(int left, int top, int width, int height, int overlap, boolean background) {
      this.left = left;
      this.top = top;
      this.width = width;
      this.height = height;
      this.overlap = overlap;
      this.background = background;
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

   public boolean isBackground() {
      return background;
   }
}
