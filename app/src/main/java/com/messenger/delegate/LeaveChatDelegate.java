package com.messenger.delegate;

import com.messenger.messengerservers.GlobalEventEmitter;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.chat.MultiUserChat;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.listeners.OnLeftChatListener;
import com.techery.spares.module.Injector;

import javax.inject.Inject;

public class LeaveChatDelegate {

    @Inject
    MessengerServerFacade facade;

    @Inject
    User user;

    private final OnLeftChatListener onLeftChatListener;
    private GlobalEventEmitter emitter;

    public LeaveChatDelegate(Injector injector, OnLeftChatListener onLeftChatListener) {
        injector.inject(this);
        this.onLeftChatListener = onLeftChatListener;
    }

    public void register() {
        emitter = facade.getGlobalEventEmitter();
        emitter.addOnLeftChatListener(onLeftChatListener);
    }

    public void unregister() {
        emitter.removeOnLeftChatListener(onLeftChatListener);
    }

    public void leave(Conversation conversation) {
        MultiUserChat chat = facade.getChatManager()
                .createMultiUserChat(conversation.getId(), facade.getOwner().getId(), isUserOwner(conversation.getOwnerId()));
        chat.leave();
    }

    private boolean isUserOwner(String ownerId) {
        return ownerId.equals(user.getId());
    }
}

