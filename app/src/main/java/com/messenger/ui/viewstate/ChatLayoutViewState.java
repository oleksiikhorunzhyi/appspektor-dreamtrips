package com.messenger.ui.viewstate;

import android.os.Parcel;

import com.messenger.app.Environment;
import com.messenger.model.ChatConversation;
import com.messenger.ui.view.ChatScreen;

public class ChatLayoutViewState extends BaseRestorableViewState<ChatScreen> {

    public enum LoadingState {
        LOADING,
        CONTENT,
        ERROR
    }

    public ChatLayoutViewState() {

    }

    private LoadingState loadingState = LoadingState.LOADING;
    private ChatConversation chatConversation;
    private Throwable error;

    public LoadingState getLoadingState() {
        return loadingState;
    }

    public void setLoadingState(LoadingState loadingState) {
        this.loadingState = loadingState;
    }

    public ChatConversation getChatConversation() {
        return chatConversation;
    }

    public void setChatConversation(ChatConversation chatConversation) {
        this.chatConversation = chatConversation;
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
        parcel.writeParcelable(chatConversation, flags);
    }

    public static final Creator<ChatLayoutViewState> CREATOR = new Creator<ChatLayoutViewState>() {
        public ChatLayoutViewState createFromParcel(Parcel source) {return new ChatLayoutViewState(source);}

        public ChatLayoutViewState[] newArray(int size) {return new ChatLayoutViewState[size];}
    };

    public ChatLayoutViewState(Parcel in) {
        loadingState = LoadingState.values()[in.readInt()];
        error = (Throwable) in.readSerializable();
        chatConversation = in.readParcelable(Environment.getChatContactsClassLoader());
    }
}