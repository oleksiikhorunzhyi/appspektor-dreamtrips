package com.messenger.ui.viewstate;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.List;

import com.messenger.app.Environment;
import com.messenger.model.ChatContacts;
import com.messenger.model.ChatUser;
import com.messenger.ui.view.NewChatScreen;

public class NewChatLayoutViewState extends BaseRestorableViewState<NewChatScreen> {

    public enum LoadingState {
        LOADING,
        CONTENT,
        ERROR
    }

    public NewChatLayoutViewState() {

    }

    private LoadingState loadingState = LoadingState.LOADING;
    private List<ChatUser> chatContacts;
    private List<ChatUser> selectedContacts;
    private Throwable error;

    public LoadingState getLoadingState() {
        return loadingState;
    }

    public void setLoadingState(LoadingState loadingState) {
        this.loadingState = loadingState;
    }

    public List<ChatUser> getChatContacts() {
        return chatContacts;
    }

    public void setChatContacts(List<ChatUser> chatContacts) {
        this.chatContacts = chatContacts;
    }

    public List<ChatUser> getSelectedContacts() {
        return selectedContacts;
    }

    public void setSelectedContacts(List<ChatUser> selectedContacts) {
        this.selectedContacts = selectedContacts;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable
    ///////////////////////////////////////////////////////////////////////////

    @Override public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(loadingState.ordinal());
        parcel.writeSerializable(error);
        parcel.writeList(chatContacts);
        parcel.writeList(selectedContacts);
    }

    public static final Creator<NewChatLayoutViewState> CREATOR = new Creator<NewChatLayoutViewState>() {
        public NewChatLayoutViewState createFromParcel(Parcel source) {return new NewChatLayoutViewState(source);}

        public NewChatLayoutViewState[] newArray(int size) {return new NewChatLayoutViewState[size];}
    };

    public NewChatLayoutViewState(Parcel in) {
        loadingState = LoadingState.values()[in.readInt()];
        error = (Throwable) in.readSerializable();
        chatContacts = in.readParcelable(Environment.getChatContactsClassLoader());
        selectedContacts = new ArrayList<>();
        in.readList(selectedContacts, Environment.getChatUserClassLoader());
    }
}