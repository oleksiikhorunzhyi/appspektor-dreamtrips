package com.messenger.delegate.command;

import com.messenger.delegate.chat.CreateChatHelper;
import com.messenger.messengerservers.chat.Chat;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import javax.inject.Inject;

import io.techery.janet.Command;
import rx.Observable;

public abstract class BaseChatCommand<Result> extends Command<Result> implements InjectableAction {

    protected final String conversationId;

    @Inject protected CreateChatHelper createChatHelper;

    protected BaseChatCommand(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getConversationId() {
        return conversationId;
    }

    protected Observable<Chat> getChat() {
        return createChatHelper.createChat(conversationId);
     }
}
