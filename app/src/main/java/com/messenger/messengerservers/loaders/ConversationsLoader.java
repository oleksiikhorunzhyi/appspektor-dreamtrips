package com.messenger.messengerservers.loaders;

import com.messenger.messengerservers.model.Conversation;

import java.util.List;

import rx.Observable;

public interface ConversationsLoader {

   Observable<List<Conversation>> load();
}
