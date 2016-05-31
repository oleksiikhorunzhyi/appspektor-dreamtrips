package com.messenger.delegate.conversation;

import com.messenger.delegate.conversation.command.SyncConversationCommand;
import com.messenger.messengerservers.model.Conversation;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.CommandActionBase;
import io.techery.janet.Janet;
import rx.Observable;
import rx.schedulers.Schedulers;

public class LoadConversationDelegate {

    private final ActionPipe<SyncConversationCommand> syncConversationPipe;

    @Inject
    public LoadConversationDelegate(Janet janet) {
        this.syncConversationPipe = janet.createPipe(SyncConversationCommand.class, Schedulers.io());
    }

    public Observable<Conversation> loadConversationFromNetwork(String conversationId) {
        return syncConversationPipe.createObservableSuccess(new SyncConversationCommand(conversationId))
                .map(CommandActionBase::getResult);
    }

}
