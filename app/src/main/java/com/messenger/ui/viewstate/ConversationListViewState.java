package com.messenger.ui.viewstate;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.messenger.app.Environment;
import com.messenger.model.ChatConversation;

import java.util.ArrayList;
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
        parcel.writeList(getData());
        parcel.writeInt(showOnlyGroupConversations ? 1 : 0);
        parcel.writeString(conversationsSearchFilter);
    }

    public static final Parcelable.Creator<NewChatLayoutViewState> CREATOR = new Parcelable.Creator<NewChatLayoutViewState>() {
        public NewChatLayoutViewState createFromParcel(Parcel source) {return new NewChatLayoutViewState(source);}

        public NewChatLayoutViewState[] newArray(int size) {return new NewChatLayoutViewState[size];}
    };

    public ConversationListViewState(Parcel in) {
        setData(new ArrayList<>());
        in.readList(getData(), Environment.getChatUserClassLoader());
        showOnlyGroupConversations = in.readInt() == 1 ? true : false;
        this.conversationsSearchFilter = in.readString();
    }
}
