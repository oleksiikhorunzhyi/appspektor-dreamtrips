package com.messenger.ui.view;

import android.content.Context;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.User;
import com.squareup.picasso.Picasso;
import com.worldventures.dreamtrips.R;

import java.util.List;

public class SingleChatSettingsScreenImpl extends ChatSettingsScreenImpl {

    public SingleChatSettingsScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SingleChatSettingsScreenImpl(Context context) {
        super(context);
    }

    @Override
    public void setConversation(Conversation conversation) {
        toolbarPresenter.setTitle(R.string.chat_settings_single_chat);
    }

    @Override
    public void setParticipants(Conversation conversation, List<User> participants) {
        singleChatAvatarView.setVisibility(View.VISIBLE);
        User addressee = participants.get(0);
        Picasso.with(getContext())
                .load(addressee.getAvatarUrl())
                .placeholder(android.R.drawable.ic_menu_compass)
                .into(singleChatAvatarView);
        chatNameTextView.setText(addressee.getName());
        chatDescriptionTextView.setText(addressee.isOnline()
                        ? R.string.chat_settings_single_chat_online
                        : R.string.chat_settings_single_chat_offline);
    }

    @Override
    protected int getLeaveChatButtonStringRes() {
        return R.string.chat_settings_row_delete_chat;
    }
}
