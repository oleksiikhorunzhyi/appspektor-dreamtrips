package com.messenger.util;

import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.Conversation$Table;
import com.messenger.util.RxContentResolver.Query.Builder;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class UnreadConversationObservable {

    private Observable<Integer> observable;

    public UnreadConversationObservable(RxContentResolver contentResolver) {
        String query = "SELECT COUNT(*) AS UNREAD_CONV FROM " + Conversation.TABLE_NAME
                + " WHERE " + Conversation$Table.UNREADMESSAGECOUNT + ">0 ";
        Builder queryBuilder = new Builder(Conversation.CONTENT_URI).withSelection(query);

        observable = contentResolver
                .query(queryBuilder.build())
                .onBackpressureLatest()
                .map(cursor -> cursor.moveToFirst() ? cursor.getInt(0) : 0)
                .distinctUntilChanged()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Subscription subscribe(Action1<Integer> action) {
        return observable.subscribe(action, throwable -> Timber.w("Can't get unread conv count"));
    }

}
