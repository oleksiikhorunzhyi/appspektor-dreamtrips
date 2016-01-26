package com.messenger.ui.viewstate;
import android.os.Parcel;
import android.os.Parcelable;

import com.messenger.messengerservers.entities.User;
import com.messenger.model.ChatUser;

import java.util.ArrayList;
import java.util.List;

public class ChatMembersScreenViewState extends LceViewState<List<ChatUser>> {

    public ChatMembersScreenViewState() {

    }

    private List<User> selectedContacts = new ArrayList<>();
    private String searchFilter;
    private boolean isChatNameEditTextVisible;

    public List<User> getSelectedContacts() {
        return selectedContacts;
    }

    public void setSelectedContacts(List<User> selectedContacts) {
        this.selectedContacts = selectedContacts;
    }

    public String getSearchFilter() {
        return searchFilter;
    }

    public void setSearchFilter(String searchFilter) {
        this.searchFilter = searchFilter;
    }

    public boolean isChatNameEditTextVisible() {
        return isChatNameEditTextVisible;
    }

    public void setChatNameEditTextVisible(boolean chatNameEditTextVisible) {
        isChatNameEditTextVisible = chatNameEditTextVisible;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable
    ///////////////////////////////////////////////////////////////////////////

    @Override public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeList(getData());
        parcel.writeList(selectedContacts);
        parcel.writeString(searchFilter);
        parcel.writeInt(isChatNameEditTextVisible ? 1 : 0);
    }

    public static final Parcelable.Creator<ChatMembersScreenViewState> CREATOR = new Parcelable.Creator<ChatMembersScreenViewState>() {
        public ChatMembersScreenViewState createFromParcel(Parcel source) {return new ChatMembersScreenViewState(source);}

        public ChatMembersScreenViewState[] newArray(int size) {return new ChatMembersScreenViewState[size];}
    };

    public ChatMembersScreenViewState(Parcel in) {
        super(in);
        setData(new ArrayList<>());
        in.readList(getData(), User.class.getClassLoader());
        selectedContacts = new ArrayList<>();
        in.readList(selectedContacts, User.class.getClassLoader());
        searchFilter = in.readString();
        isChatNameEditTextVisible = in.readInt() == 1;
    }
}