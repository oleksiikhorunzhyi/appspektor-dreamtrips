package com.messenger.ui.viewstate;

import android.os.Parcel;
import android.os.Parcelable;

import com.messenger.app.Environment;
import com.messenger.model.ChatConversation;

public class ChatLayoutViewState extends LceViewState<ChatConversation> {

    public ChatLayoutViewState() {
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable
    ///////////////////////////////////////////////////////////////////////////

    @Override public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeParcelable(getData(), flags);
    }

    public static final Parcelable.Creator<ChatLayoutViewState> CREATOR = new Parcelable.Creator<ChatLayoutViewState>() {
        public ChatLayoutViewState createFromParcel(Parcel source) {return new ChatLayoutViewState(source);}

        public ChatLayoutViewState[] newArray(int size) {return new ChatLayoutViewState[size];}
    };

    public ChatLayoutViewState(Parcel in) {
        super(in);
        setData(in.readParcelable(Environment.getChatConversationClassLoader()));
    }
}