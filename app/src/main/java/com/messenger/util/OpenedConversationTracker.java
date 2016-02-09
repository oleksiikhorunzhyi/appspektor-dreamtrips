package com.messenger.util;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.subjects.ReplaySubject;
import timber.log.Timber;

public class OpenedConversationTracker {

    private ReplaySubject<String> stream = ReplaySubject.create(1);
    private Map<String, Integer> openedConversationIds;

    public OpenedConversationTracker() {
        //expected max opened 2 conversation at he same time from push notifications
        openedConversationIds = new HashMap<>(2);
    }

    public void addOpenedConversation(String conversationId) {
        Timber.i("Track conversation %s", conversationId);
        Integer prevCount = openedConversationIds.get(conversationId);
        if (prevCount == null) prevCount = 0;
        openedConversationIds.put(conversationId, prevCount + 1);
        //
        stream.onNext(conversationId);
    }

    public void removeOpenedConversation(String conversationId) {
        Timber.i("Untrack conversation %s", conversationId);
        Integer prevCount = openedConversationIds.get(conversationId);
        if (prevCount == null || prevCount <= 0) {
            openedConversationIds.remove(conversationId);
            return;
        }
        openedConversationIds.put(conversationId, prevCount - 1);
    }

    public String getOpenedConversationId() {
        return stream.getValue();
    }

    public Observable<String> watchOpenedConversationId() {
                return stream.asObservable().filter(id -> containsOpenedConversationId(id));
            }

    public boolean containsOpenedConversationId(String conversationId) {
        Integer prevCount = openedConversationIds.get(conversationId);
        return prevCount != null && prevCount > 0;
    }

}
