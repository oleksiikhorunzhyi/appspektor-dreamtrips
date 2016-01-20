package com.messenger.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.User;

import java.util.List;

public class TripChatSettingsScreenImpl extends GroupChatSettingsScreenImpl {

    public TripChatSettingsScreenImpl(Context context, String conversationId) {
        super(context, conversationId);
    }

    public TripChatSettingsScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setParticipants(Conversation conversation, List<User> participants) {
        super.setParticipants(conversation, participants);
        groupAvatarsView.setVisibility(View.GONE);
        groupPicView.setVisibility(VISIBLE);
    }

}
