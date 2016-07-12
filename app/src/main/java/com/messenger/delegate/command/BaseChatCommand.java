package com.messenger.delegate.command;

import com.messenger.delegate.chat.CreateChatHelper;
import com.messenger.messengerservers.chat.AccessConversationDeniedException;
import com.messenger.messengerservers.chat.Chat;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.ui.helper.ConversationHelper;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import javax.inject.Inject;

import io.techery.janet.Command;
import rx.Observable;

public abstract class BaseChatCommand<Result> extends Command<Result> implements InjectableAction {

    protected final String conversationId;

    @Inject protected CreateChatHelper createChatHelper;
    @Inject protected ConversationsDAO conversationsDAO;

    protected BaseChatCommand(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getConversationId() {
        return conversationId;
    }

    protected Observable<Chat> getChat() {
        return conversationsDAO.getConversation(conversationId)
                .take(1)
                .flatMap(dataConversation -> {
                    if (ConversationHelper.isAbandoned(dataConversation)) {
                        return Observable.error(new AccessConversationDeniedException());
                    } else return createChatHelper.createChat(conversationId);
                });
    }
}
