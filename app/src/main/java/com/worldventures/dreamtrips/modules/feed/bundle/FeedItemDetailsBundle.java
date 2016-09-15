package com.worldventures.dreamtrips.modules.feed.bundle;

import android.os.Parcel;

import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;

public class FeedItemDetailsBundle implements FeedDetailsBundle {

   private final boolean slave;
   private final boolean showAdditionalInfo;
   private final boolean openKeyboard;
   private final FeedItem feedItem;

   private FeedItemDetailsBundle(FeedItem feedItem, boolean slave, boolean showAdditionalInfo, boolean openKeyboard) {
      this.feedItem = feedItem;
      this.slave = slave;
      this.showAdditionalInfo = showAdditionalInfo;
      this.openKeyboard = openKeyboard;
   }

   @Override
   public FeedItem getFeedItem() {
      return feedItem;
   }

   @Override
   public boolean isSlave() {
      return slave;
   }

   @Override
   public boolean shouldShowAdditionalInfo() {
      return showAdditionalInfo;
   }

   @Override
   public FeedEntity getFeedEntity() {
      return feedItem.getItem();
   }

   @Override
   public boolean shouldOpenKeyboard() {
      return openKeyboard;
   }

   @Override
   public boolean shouldShowLikersPanel() {
      return false;
   }

   public static class Builder {

      private FeedItem feedItem;
      private boolean slave;
      private boolean showAdditionalInfo;
      private boolean openKeyboard;

      public Builder feedItem(FeedItem feedItem) {
         this.feedItem = feedItem;
         return this;
      }

      public Builder slave(boolean slave) {
         this.slave = slave;
         return this;
      }

      public Builder showAdditionalInfo(boolean showAdditionalInfo) {
         this.showAdditionalInfo = showAdditionalInfo;
         return this;
      }

      public Builder openKeyboard(boolean openKeyboard) {
         this.openKeyboard = openKeyboard;
         return this;
      }

      public FeedItemDetailsBundle build() {
         return new FeedItemDetailsBundle(feedItem, slave, showAdditionalInfo, openKeyboard);
      }
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeByte(this.slave ? (byte) 1 : (byte) 0);
      dest.writeByte(this.showAdditionalInfo ? (byte) 1 : (byte) 0);
      dest.writeByte(this.openKeyboard ? (byte) 1 : (byte) 0);
      dest.writeParcelable(this.feedItem, flags);
   }

   protected FeedItemDetailsBundle(Parcel in) {
      this.slave = in.readByte() != 0;
      this.showAdditionalInfo = in.readByte() != 0;
      this.openKeyboard = in.readByte() != 0;
      this.feedItem = in.readParcelable(FeedItem.class.getClassLoader());
   }

   public static final Creator<FeedItemDetailsBundle> CREATOR = new Creator<FeedItemDetailsBundle>() {
      @Override
      public FeedItemDetailsBundle createFromParcel(Parcel source) {
         return new FeedItemDetailsBundle(source);
      }

      @Override
      public FeedItemDetailsBundle[] newArray(int size) {
         return new FeedItemDetailsBundle[size];
      }
   };
}
