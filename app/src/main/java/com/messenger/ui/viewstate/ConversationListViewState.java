package com.messenger.ui.viewstate;

import android.os.Parcel;
import android.os.Parcelable;

import com.messenger.app.Environment;
import com.messenger.model.ChatConversation;

import java.util.ArrayList;
import java.util.List;

public class ConversationListViewState extends LceViewState<List<ChatConversation>> {

    public ConversationListViewState() {

    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable
    ///////////////////////////////////////////////////////////////////////////

    @Override public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeList(getData());
    }

    public static final Parcelable.Creator<NewChatLayoutViewState> CREATOR = new Parcelable.Creator<NewChatLayoutViewState>() {
        public NewChatLayoutViewState createFromParcel(Parcel source) {return new NewChatLayoutViewState(source);}

        public NewChatLayoutViewState[] newArray(int size) {return new NewChatLayoutViewState[size];}
    };

    public ConversationListViewState(Parcel in) {
        setData(new ArrayList<>());
        in.readList(getData(), Environment.getChatUserClassLoader());
    }
}
