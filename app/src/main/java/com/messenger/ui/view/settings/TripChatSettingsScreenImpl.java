package com.messenger.ui.view.settings;

import android.content.Context;
import android.util.AttributeSet;

import com.messenger.entities.DataConversation;
import com.messenger.entities.DataUser;

import java.util.List;

public class TripChatSettingsScreenImpl extends GroupChatSettingsScreenImpl<TripSettingsPath> {

    public TripChatSettingsScreenImpl(Context context) {
        super(context);
    }

    public TripChatSettingsScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setParticipants(DataConversation conversation, List<DataUser> participants) {
        super.setParticipants(conversation, participants);
        groupAvatarsView.setVisibility(GONE);
        groupPicView.setVisibility(VISIBLE);
    }

}
