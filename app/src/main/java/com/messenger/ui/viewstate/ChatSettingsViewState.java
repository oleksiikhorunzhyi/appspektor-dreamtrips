package com.messenger.ui.viewstate;

import android.os.Parcel;
import android.os.Parcelable;

import com.messenger.app.Environment;

public class ChatSettingsViewState extends LceViewState<Parcelable> {

    public ChatSettingsViewState() {
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable
    ///////////////////////////////////////////////////////////////////////////

    @Override public void writeToParcel(Parcel parcel, int flags) {
    }

    public static final Parcelable.Creator<ChatSettingsViewState> CREATOR = new Parcelable.Creator<ChatSettingsViewState>() {
        public ChatSettingsViewState createFromParcel(Parcel source) {return new ChatSettingsViewState(source);}

        public ChatSettingsViewState[] newArray(int size) {return new ChatSettingsViewState[size];}
    };

    public ChatSettingsViewState(Parcel in) {
        super(in);
    }
}
