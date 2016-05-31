package com.messenger.util.serverfacade;

import com.messenger.messengerservers.loaders.ConversationsLoader;
import com.messenger.messengerservers.model.Conversation;

import java.util.List;

import rx.Observable;

public class MockConversationsLoader implements ConversationsLoader {

    private List<Conversation> conversations;

    public MockConversationsLoader(List<Conversation> conversations) {
        this.conversations = conversations;
    }

    @Override
    public Observable<List<Conversation>> load() {
        return Observable.just(conversations);
    }
}
