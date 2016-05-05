package com.messenger.messengerservers.loaders;

import com.messenger.messengerservers.model.Conversation;

import java.util.List;

import rx.Observable;

public interface ConversationLoader {

    Observable<Conversation> load();
}
