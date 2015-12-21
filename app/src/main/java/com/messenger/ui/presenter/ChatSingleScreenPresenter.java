package com.messenger.ui.presenter;

import android.content.Context;
import android.content.Intent;

import com.messenger.messengerservers.ChatManager;
import com.messenger.messengerservers.chat.Chat;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.User;
import com.raizlabs.android.dbflow.sql.SqlUtils;

public class ChatSingleScreenPresenter extends ChatScreenPresenterImpl {

    public ChatSingleScreenPresenter(Context context, Intent startIntent) {
        super(context, startIntent);
    }

    @Override
    protected Chat createChat(ChatManager chatManager, Conversation conversation) {
        String query = "SELECT * FROM Users u " +
                "JOIN ParticipantsRelationship p " +
                "ON p.userId = u._id " +
                "WHERE p.conversationId = ?";
        User mate = SqlUtils.querySingle(User.class, query, new String[]{conversation.getId()});
        return chatManager.createSingleUserChat(mate.getId(), conversation.getId());
    }
}
