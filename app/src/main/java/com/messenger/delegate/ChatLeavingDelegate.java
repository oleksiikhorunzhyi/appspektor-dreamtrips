package com.messenger.delegate;

import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.chat.MultiUserChat;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.listeners.OnChatLeftListener;
import com.messenger.ui.helper.ConversationHelper;
import com.techery.spares.module.Injector;

import javax.inject.Inject;

public class ChatLeavingDelegate {

    @Inject
    MessengerServerFacade facade;
    @Inject
    User user;

    private final OnChatLeftListener listener;
    private final ConversationHelper conversationHelper;

    public ChatLeavingDelegate(Injector injector, OnChatLeftListener listener) {
        injector.inject(this);
        this.listener = listener;
        conversationHelper = new ConversationHelper();
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

    public void leave(Conversation conversation) {
        MultiUserChat chat = facade.getChatManager().createMultiUserChat(
                conversation.getId(),
                facade.getOwner().getId(),
                conversationHelper.isOwner(conversation, user)
        );
        chat.leave();
    }

}

