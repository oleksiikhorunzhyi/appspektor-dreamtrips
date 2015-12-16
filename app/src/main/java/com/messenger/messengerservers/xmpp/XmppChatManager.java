package com.messenger.messengerservers.xmpp;

import android.support.annotation.Nullable;

import com.messenger.messengerservers.ChatManager;
import com.messenger.messengerservers.chat.MultiUserChat;
import com.messenger.messengerservers.chat.SingleUserChat;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.xmpp.chats.XmppMultiUserChat;
import com.messenger.messengerservers.xmpp.chats.XmppSingleUserChat;

public class XmppChatManager implements ChatManager {
    private final XmppServerFacade facade;

    public XmppChatManager(XmppServerFacade facade) {
        this.facade = facade;
    }

    @Override
    public SingleUserChat createSingleUserChat(@Nullable String companionId, @Nullable String conversationId) {
        return new XmppSingleUserChat(facade, companionId, conversationId);
    }

    @Override
    public MultiUserChat createMultiUserChat(User owner, @Nullable String roomId) {
        return new XmppMultiUserChat(facade, owner, roomId);
    }
}
