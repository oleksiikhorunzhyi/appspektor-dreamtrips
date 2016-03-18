package com.messenger.util;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.subjects.PublishSubject;
import timber.log.Timber;

public class OpenedConversationTracker {

    private PublishSubject<String> streamWriter = PublishSubject.create();
    private Observable<String> streamReader = streamWriter.replay(1).autoConnect();

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
        streamWriter.onNext(conversationId);
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

    public Observable<String> watchOpenedConversationId() {
                return streamReader.asObservable().filter(id -> containsOpenedConversationId(id));
            }

    public boolean containsOpenedConversationId(String conversationId) {
        Integer prevCount = openedConversationIds.get(conversationId);
        return prevCount != null && prevCount > 0;
    }

}
