package com.messenger.ui.viewstate;

import android.os.Parcel;
import android.os.Parcelable;

public class EditChatMembersViewState extends LceViewState<Parcelable> {

   public EditChatMembersViewState() {
   }

   private String searchFilter;

   public String getSearchFilter() {
      return searchFilter;
   }

   public void setSearchFilter(String searchFilter) {
      this.searchFilter = searchFilter;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Parcelable
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public void writeToParcel(Parcel parcel, int flags) {
      super.writeToParcel(parcel, flags);
      parcel.writeString(searchFilter);
   }

   public static final Creator<EditChatMembersViewState> CREATOR = new Creator<EditChatMembersViewState>() {
      public EditChatMembersViewState createFromParcel(Parcel source) {return new EditChatMembersViewState(source);}

      public EditChatMembersViewState[] newArray(int size) {return new EditChatMembersViewState[size];}
   };

   public EditChatMembersViewState(Parcel in) {
      super(in);
      searchFilter = in.readString();
   }
}
