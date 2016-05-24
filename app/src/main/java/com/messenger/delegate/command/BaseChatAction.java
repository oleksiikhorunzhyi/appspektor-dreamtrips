package com.messenger.delegate.command;

import com.messenger.delegate.chat.CreateChatHelper;
import com.messenger.messengerservers.chat.Chat;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import javax.inject.Inject;

import io.techery.janet.CommandActionBase;
import rx.Observable;

public abstract class BaseChatAction<Result> extends CommandActionBase<Result> implements InjectableAction {

    protected final String conversationId;

    @Inject protected CreateChatHelper createChatHelper;

    protected BaseChatAction(String conversationId) {
        this.conversationId = conversationId;
    }

    public void setCreateChatHelper(CreateChatHelper createChatHelper) {
        this.createChatHelper = createChatHelper;
    }

    public String getConversationId() {
        return conversationId;
    }

    protected Observable<Chat> getChat() {
        return createChatHelper.createChat(conversationId);
     }
}
