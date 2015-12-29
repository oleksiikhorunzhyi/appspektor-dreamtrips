package com.messenger.ui.viewstate;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.messenger.model.ChatConversation;

import java.util.List;

public class ConversationListViewState extends LceViewState<List<ChatConversation>> {

    public ConversationListViewState() {
    }

    private boolean showOnlyGroupConversations;
    private String conversationsSearchFilter;
    private Cursor cursor;

    public boolean isShowOnlyGroupConversations() {
        return showOnlyGroupConversations;
    }

    public void setShowOnlyGroupConversations(boolean showOnlyGroupConversations) {
        this.showOnlyGroupConversations = showOnlyGroupConversations;
    }

    public String getConversationsSearchFilter() {
        return conversationsSearchFilter;
    }

    public void setConversationsSearchFilter(String conversationsSearchFilter) {
        this.conversationsSearchFilter = conversationsSearchFilter;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable
    ///////////////////////////////////////////////////////////////////////////

    @Override public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeInt(showOnlyGroupConversations ? 1 : 0);
        parcel.writeString(conversationsSearchFilter);
    }

    public static final Parcelable.Creator<ConversationListViewState> CREATOR = new Parcelable.Creator<ConversationListViewState>() {
        public ConversationListViewState createFromParcel(Parcel source) {return new ConversationListViewState(source);}

        public ConversationListViewState[] newArray(int size) {return new ConversationListViewState[size];}
    };

    public ConversationListViewState(Parcel in) {
        super(in);
        showOnlyGroupConversations = in.readInt() == 1 ? true : false;
        this.conversationsSearchFilter = in.readString();
    }
}
