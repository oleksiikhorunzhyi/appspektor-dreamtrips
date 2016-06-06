package com.messenger.delegate.chat;

import com.messenger.entities.DataConversation;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.chat.GroupChat;
import com.techery.spares.module.Injector;

import javax.inject.Inject;

public class ChatLeavingDelegate {

    @Inject MessengerServerFacade facade;

    public ChatLeavingDelegate(Injector injector) {
        injector.inject(this);
    }

    public void leave(DataConversation conversation) {
        GroupChat chat = facade.getChatManager().createGroupChat(
                conversation.getId(), conversation.getOwnerId());
        chat.leave();
    }

}

