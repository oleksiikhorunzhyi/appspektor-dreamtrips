package com.messenger.ui.view.settings;

import android.content.Context;
import android.util.AttributeSet;

import com.messenger.entities.Conversation;
import com.messenger.entities.User;

import java.util.List;

public class TripChatSettingsScreenImpl extends GroupChatSettingsScreenImpl<TripSettingsPath> {

    public TripChatSettingsScreenImpl(Context context) {
        super(context);
    }

    public TripChatSettingsScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setParticipants(Conversation conversation, List<User> participants) {
        super.setParticipants(conversation, participants);
        groupAvatarsView.setVisibility(GONE);
        groupPicView.setVisibility(VISIBLE);
    }

}
