package com.worldventures.dreamtrips.modules.feed.bundle;

import android.os.Parcel;

import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;

public class CommentsBundle implements CommentableBundle {

   protected FeedEntity feedEntity;
   protected boolean openKeyboard;
   protected boolean showLikersPanel;

   public CommentsBundle(FeedEntity feedEntity, boolean openKeyboard, boolean showLikers) {
      this.feedEntity = feedEntity;
      this.openKeyboard = openKeyboard;
      this.showLikersPanel = showLikers;
   }

   @Override
   public FeedEntity getFeedEntity() {
      return feedEntity;
   }

   @Override
   public boolean shouldOpenKeyboard() {
      return openKeyboard;
   }

   @Override
   public boolean shouldShowLikersPanel() {
      return showLikersPanel;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Parcelable
   ///////////////////////////////////////////////////////////////////////////


   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeSerializable(this.feedEntity);
      dest.writeByte(this.openKeyboard ? (byte) 1 : (byte) 0);
      dest.writeByte(this.showLikersPanel ? (byte) 1 : (byte) 0);
   }

   protected CommentsBundle(Parcel in) {
      this.feedEntity = (FeedEntity) in.readSerializable();
      this.openKeyboard = in.readByte() != 0;
      this.showLikersPanel = in.readByte() != 0;
   }

   public static final Creator<CommentsBundle> CREATOR = new Creator<CommentsBundle>() {
      @Override
      public CommentsBundle createFromParcel(Parcel source) {
         return new CommentsBundle(source);
      }

      @Override
      public CommentsBundle[] newArray(int size) {
         return new CommentsBundle[size];
      }
   };
}
