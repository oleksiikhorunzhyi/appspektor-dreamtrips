package com.messenger.util;

import rx.Observable;
import rx.subjects.ReplaySubject;

public class OpenedConversationTracker {

    private ReplaySubject<String> replaySubject = ReplaySubject.create(1);

    public void setOpenedConversation(String openedConversationId) {
        replaySubject.onNext(openedConversationId);
    }

    public String getOpenedConversationId() {
        return replaySubject.getValue();
    }

    public Observable<String> watchOpenedConversationId() {
        return replaySubject.asObservable();
    }

}
