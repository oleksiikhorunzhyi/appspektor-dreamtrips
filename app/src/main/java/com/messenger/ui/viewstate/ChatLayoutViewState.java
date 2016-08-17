package com.messenger.ui.viewstate;

import android.os.Parcel;
import android.os.Parcelable;

import com.messenger.entities.DataConversation;

public class ChatLayoutViewState extends LceViewState<DataConversation> {

   public ChatLayoutViewState() {
   }

   ///////////////////////////////////////////////////////////////////////////
   // Parcelable
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public void writeToParcel(Parcel parcel, int flags) {
      super.writeToParcel(parcel, flags);
   }

   public static final Parcelable.Creator<ChatLayoutViewState> CREATOR = new Parcelable.Creator<ChatLayoutViewState>() {
      public ChatLayoutViewState createFromParcel(Parcel source) {return new ChatLayoutViewState(source);}

      public ChatLayoutViewState[] newArray(int size) {return new ChatLayoutViewState[size];}
   };

   public ChatLayoutViewState(Parcel in) {
      super(in);
   }
}