package com.messenger.delegate.command;

import com.messenger.delegate.chat.CreateChatHelper;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.chat.Chat;
import com.messenger.storage.dao.ConversationsDAO;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import javax.inject.Inject;

import io.techery.janet.CommandActionBase;
import rx.Observable;

public abstract class BaseChatAction<Result> extends CommandActionBase<Result> implements InjectableAction {
    protected final String conversationId;

    @Inject protected MessengerServerFacade messengerServerFacade;
    @Inject protected ConversationsDAO conversationsDAO;
    @Inject protected CreateChatHelper createChatHelper;

    protected BaseChatAction(String conversationId) {
        this.conversationId = conversationId;
    }

    public void setMessengerServerFacade(MessengerServerFacade messengerServerFacade) {
        this.messengerServerFacade = messengerServerFacade;
    }

    public String getConversationId() {
        return conversationId;
    }

    protected Observable<Chat> getChat() {
        return conversationsDAO.getConversationWithParticipants(conversationId)
                .take(1)
                .flatMap(pair -> createChatHelper.createChat(pair.first, pair.second));
     }
}
