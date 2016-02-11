package com.messenger.util;

import com.messenger.storage.dao.ConversationsDAO;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import timber.log.Timber;

public class UnreadConversationObservable {

    private Observable<Integer> observable;

    public UnreadConversationObservable(ConversationsDAO conversationsDAO) {
        observable = conversationsDAO.getUnreadConversationsCount()
                .onBackpressureLatest()
                .distinctUntilChanged()
                .compose(new IoToMainComposer<>());
    }

    public Subscription subscribe(Action1<Integer> action) {
        return observable.subscribe(action, throwable -> Timber.w("Can't get unread conv count"));
    }

}