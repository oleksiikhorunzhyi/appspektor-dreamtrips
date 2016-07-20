package com.worldventures.dreamtrips.messenger.util.serverfacade;

import com.messenger.messengerservers.loaders.ConversationLoader;
import com.messenger.messengerservers.model.Conversation;

import rx.Observable;

public class MockConversationLoader implements ConversationLoader {

    private Conversation conversation;

    public MockConversationLoader(Conversation conversation) {
        this.conversation = conversation;
    }

    @Override
    public Observable<Conversation> load() {
        return Observable.just(conversation);
    }
}
