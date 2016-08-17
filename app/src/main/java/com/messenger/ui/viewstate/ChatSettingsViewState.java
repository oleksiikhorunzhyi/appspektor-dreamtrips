package com.messenger.ui.viewstate;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

public class ChatSettingsViewState extends LceViewState<Parcelable> {

   private UploadingState uploadAvatar;

   public ChatSettingsViewState() {
   }


   @Nullable
   public UploadingState getUploadAvatar() {
      return uploadAvatar;
   }

   public void setUploadAvatar(@Nullable UploadingState uploadAvatar) {
      this.uploadAvatar = uploadAvatar;
   }

   public enum UploadingState {
      UPLOADING, ERROR, UPLOADED
   }
   ///////////////////////////////////////////////////////////////////////////
   // Parcelable
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public void writeToParcel(Parcel parcel, int flags) {
      super.writeToParcel(parcel, flags);
   }

   public static final Parcelable.Creator<ChatSettingsViewState> CREATOR = new Parcelable.Creator<ChatSettingsViewState>() {
      public ChatSettingsViewState createFromParcel(Parcel source) {return new ChatSettingsViewState(source);}

      public ChatSettingsViewState[] newArray(int size) {return new ChatSettingsViewState[size];}
   };

   public ChatSettingsViewState(Parcel in) {
      super(in);
   }
}
