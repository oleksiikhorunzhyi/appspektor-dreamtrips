package com.messenger.delegate.chat;

import com.messenger.entities.DataConversation;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.chat.MultiUserChat;
import com.messenger.messengerservers.listeners.OnChatLeftListener;
import com.messenger.ui.helper.ConversationHelper;
import com.techery.spares.module.Injector;

import javax.inject.Inject;

public class ChatLeavingDelegate {

    @Inject
    MessengerServerFacade facade;
    @Inject
    DataUser user;

    private final OnChatLeftListener listener;

    public ChatLeavingDelegate(Injector injector, OnChatLeftListener listener) {
        injector.inject(this);
        this.listener = listener;
    }

    public void register() {
        if (listener != null) {
            facade.getGlobalEventEmitter().addOnChatLeftListener(listener);
        }
    }

    public void unregister() {
        if (listener != null) {
            facade.getGlobalEventEmitter().removeOnChatLeftListener(listener);
        }
    }

    public void leave(DataConversation conversation) {
        MultiUserChat chat = facade.getChatManager().createMultiUserChat(
                conversation.getId(),
                facade.getUsername(),
                ConversationHelper.isOwner(conversation, user)
        );
        chat.leave();
        chat.close();
    }

}

