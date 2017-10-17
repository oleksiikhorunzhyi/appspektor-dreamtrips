package com.worldventures.dreamtrips.wallet.ui.dashboard.util.model;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;

public class TransitionModel implements Parcelable {

   private final boolean defaultCard;
   private final int left;
   private final int top;
   private final int width;
   private final int height;
   private final int overlap;
   private final @DrawableRes int background;

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

   protected TransitionModel(Parcel in) {
      defaultCard = in.readByte() != 0;
      left = in.readInt();
      top = in.readInt();
      width = in.readInt();
      height = in.readInt();
      overlap = in.readInt();
      background = in.readInt();
   }

   public static final Creator<TransitionModel> CREATOR = new Creator<TransitionModel>() {
      @Override
      public TransitionModel createFromParcel(Parcel in) {
         return new TransitionModel(in);
      }

      @Override
      public TransitionModel[] newArray(int size) {
         return new TransitionModel[size];
      }
   };

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

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeByte((byte) (defaultCard ? 1 : 0));
      dest.writeInt(left);
      dest.writeInt(top);
      dest.writeInt(width);
      dest.writeInt(height);
      dest.writeInt(overlap);
      dest.writeInt(background);
   }
}
