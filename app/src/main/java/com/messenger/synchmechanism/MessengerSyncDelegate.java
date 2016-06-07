package com.messenger.synchmechanism;

import com.messenger.delegate.conversation.command.SyncConversationsCommand;
import com.messenger.delegate.roster.LoadContactsCommand;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.Observable;
import rx.schedulers.Schedulers;

public class MessengerSyncDelegate {

    private final ActionPipe<LoadContactsCommand> contactsPipe;
    private final ActionPipe<SyncConversationsCommand> conversationsPipe;

    @Inject
    public MessengerSyncDelegate(Janet janet) {
        this.contactsPipe = janet.createPipe(LoadContactsCommand.class, Schedulers.io());
        this.conversationsPipe = janet.createPipe(SyncConversationsCommand.class, Schedulers.io());
    }

    public Observable<Boolean> sync() {
        return Observable.zip(syncContacts(), syncConversations(),
                (loadContactsCommand, loadConversationsCommand) -> true);
    }

    public ActionPipe<LoadContactsCommand> getContactsPipe() {
        return contactsPipe;
    }

    public ActionPipe<SyncConversationsCommand> getConversationsPipe() {
        return conversationsPipe;
    }

    private Observable<LoadContactsCommand> syncContacts() {
        return contactsPipe
                .createObservableResult(new LoadContactsCommand());
    }

    private Observable<SyncConversationsCommand> syncConversations() {
        return conversationsPipe
                .createObservableResult(new SyncConversationsCommand());
    }

}
