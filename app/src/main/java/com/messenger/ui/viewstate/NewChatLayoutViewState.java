package com.messenger.ui.viewstate;

import android.os.Parcel;
import android.os.Parcelable;

import com.messenger.app.Environment;
import com.messenger.model.ChatUser;

import java.util.ArrayList;
import java.util.List;

public class NewChatLayoutViewState extends LceViewState<List<ChatUser>> {

    public NewChatLayoutViewState() {

    }

    private List<ChatUser> selectedContacts = new ArrayList<>();

    public List<ChatUser> getSelectedContacts() {
        return selectedContacts;
    }

    public void setSelectedContacts(List<ChatUser> selectedContacts) {
        this.selectedContacts = selectedContacts;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable
    ///////////////////////////////////////////////////////////////////////////

    @Override public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeList(getData());
        parcel.writeList(selectedContacts);
    }

    public static final Parcelable.Creator<NewChatLayoutViewState> CREATOR = new Parcelable.Creator<NewChatLayoutViewState>() {
        public NewChatLayoutViewState createFromParcel(Parcel source) {return new NewChatLayoutViewState(source);}

        public NewChatLayoutViewState[] newArray(int size) {return new NewChatLayoutViewState[size];}
    };

    public NewChatLayoutViewState(Parcel in) {
        super(in);
        setData(new ArrayList<>());
        in.readList(getData(), Environment.getChatUserClassLoader());
        selectedContacts = new ArrayList<>();
        in.readList(selectedContacts, Environment.getChatUserClassLoader());
    }
}