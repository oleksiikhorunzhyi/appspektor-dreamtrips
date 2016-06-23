package com.messenger.synchmechanism;

import com.messenger.delegate.conversation.command.SyncConversationsCommand;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.Observable;
import rx.schedulers.Schedulers;

public class MessengerSyncDelegate {

    private final ActionPipe<SyncConversationsCommand> conversationsPipe;

    @Inject
    public MessengerSyncDelegate(Janet janet) {
        this.conversationsPipe = janet.createPipe(SyncConversationsCommand.class, Schedulers.io());
    }

    public Observable<Boolean> sync() {
        return syncConversations()
                .map(syncConversationsCommand -> true);
    }

    public ActionPipe<SyncConversationsCommand> getConversationsPipe() {
        return conversationsPipe;
    }

    private Observable<SyncConversationsCommand> syncConversations() {
        return conversationsPipe
                .createObservableResult(new SyncConversationsCommand());
    }

}
