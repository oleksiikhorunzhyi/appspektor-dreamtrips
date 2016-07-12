package com.messenger.delegate.conversation;

import com.messenger.delegate.conversation.command.SyncConversationCommand;
import com.messenger.entities.DataConversation;
import com.messenger.messengerservers.model.Conversation;
import com.messenger.storage.dao.ConversationsDAO;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import rx.Observable;
import rx.schedulers.Schedulers;

public class LoadConversationDelegate {

    private final ActionPipe<SyncConversationCommand> syncConversationPipe;
    private final ConversationsDAO conversationsDAO;

    @Inject
    public LoadConversationDelegate(Janet janet, ConversationsDAO conversationsDAO) {
        this.syncConversationPipe = janet.createPipe(SyncConversationCommand.class, Schedulers.io());
        this.conversationsDAO = conversationsDAO;
    }

    public ActionPipe<SyncConversationCommand> getSyncConversationPipe() {
        return syncConversationPipe;
    }

    public Observable<Conversation> loadConversationFromNetwork(String conversationId) {
        return syncConversationPipe.createObservableResult(new SyncConversationCommand(conversationId))
                .map(Command::getResult);
    }

    public Observable<DataConversation> loadConversationFromDb(String conversationId) {
        return conversationsDAO
                .getConversation(conversationId)
                .take(1);
    }

    public Observable<DataConversation> loadConversationFromNetworkAndRefreshFromDb(String conversationId) {
        return loadConversationFromNetwork(conversationId)
                .flatMap(conversation -> loadConversationFromDb(conversationId));
    }
}
