package com.messenger.ui.viewstate;

import android.os.Parcel;

import com.messenger.entities.DataConversation;
import com.messenger.ui.presenter.ConversationListScreenPresenter.ChatTypeItem.ChatsType;

import java.util.List;

public class ConversationListViewState extends LceViewState<List<DataConversation>> {

   private String chatType;
   private String searchFilter;

   public ConversationListViewState() {
   }

   public
   @ChatsType
   String getChatType() {
      return chatType;
   }

   public void setChatType(@ChatsType String type) {
      this.chatType = type;
   }

   public String getSearchFilter() {
      return searchFilter;
   }

   public void setSearchFilter(String conversationsSearchFilter) {
      this.searchFilter = conversationsSearchFilter;
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
      super.writeToParcel(dest, flags);
      dest.writeString(this.chatType);
      dest.writeString(this.searchFilter);
   }

   protected ConversationListViewState(Parcel in) {
      super(in);
      this.chatType = in.readString();
      this.searchFilter = in.readString();
   }

   public static final Creator<ConversationListViewState> CREATOR = new Creator<ConversationListViewState>() {
      public ConversationListViewState createFromParcel(Parcel source) {
         return new ConversationListViewState(source);
      }

      public ConversationListViewState[] newArray(int size) {
         return new ConversationListViewState[size];
      }
   };
}
