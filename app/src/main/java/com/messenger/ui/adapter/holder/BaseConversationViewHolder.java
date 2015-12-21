package com.messenger.ui.adapter.holder;

import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.innahema.collections.query.functions.Action1;
import com.messenger.messengerservers.entities.ParticipantsRelationship;
import com.messenger.messengerservers.entities.User;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.InjectView;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class BaseConversationViewHolder extends BaseViewHolder {

    @InjectView(R.id.conversation_name_textview)
    TextView nameTextView;
    @InjectView(R.id.conversation_last_message_textview)
    TextView lastMessageTextView;
    @InjectView(R.id.conversation_last_message_date_textview)
    TextView lastMessageDateTextView;
    @InjectView(R.id.conversation_unread_messages_count_textview)
    TextView unreadMessagesCountTextView;
    private Subscription participantsSubscriber;

    public BaseConversationViewHolder(View itemView) {
        super(itemView);
    }

    public TextView getNameTextView() {
        return nameTextView;
    }

    public TextView getLastMessageTextView() {
        return lastMessageTextView;
    }

    public TextView getLastMessageDateTextView() {
        return lastMessageDateTextView;
    }

    public TextView getUnreadMessagesCountTextView() {
        return unreadMessagesCountTextView;
    }

    public void updateParticipants(String conversationId, Action1<List<User>> listener) {
        if (participantsSubscriber != null && !participantsSubscriber.isUnsubscribed()) {
            participantsSubscriber.unsubscribe();
        }
        participantsSubscriber = Observable.defer(() -> {
            String query = "SELECT * FROM Users u " +
                    "JOIN ParticipantsRelationship p " +
                    "ON p.userId = u._id " +
                    "WHERE p.conversationId = ?";
            final ContentObserver[] contentObserver = {null};
            return Observable.<Cursor>create(subscriber -> {
                        contentObserver[0] = new ContentObserver(null) {
                            @Override
                            public void onChange(boolean selfChange) {
                                if (!subscriber.isUnsubscribed()) {
                                    tryFetchCursor(query, new String[]{conversationId}, subscriber);
                                } else {
                                    unsubscribeFromContentUpdates(this);
                                }
                            }
                        };
                        subscribeToContentUpdates(ParticipantsRelationship.CONTENT_URI, contentObserver[0]);
                        subscribeToContentUpdates(User.CONTENT_URI, contentObserver[0]);
                        tryFetchCursor(query, new String[]{conversationId}, subscriber);
                    }
            ).doOnUnsubscribe(() -> {
                if (contentObserver[0] != null) unsubscribeFromContentUpdates(contentObserver[0]);
            });
        })
                .throttleLast(100, TimeUnit.MILLISECONDS)
                .map(c -> SqlUtils.convertToList(User.class, c))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycle.bindView(itemView))
                .subscribe(users -> {
                    setParticipants(users);
                    listener.apply(users);
                });
    }

    private void tryFetchCursor(String query, String[] selectionArgs, Subscriber<? super Cursor> subscriber) {
        try {
            subscriber.onNext(FlowManager.getDatabaseForTable(User.class).getWritableDatabase().rawQuery(query, selectionArgs));
        } catch (Exception e) {
            subscriber.onError(e);
        }
    }

    private void subscribeToContentUpdates(Uri uri, ContentObserver contentObserver) {
        itemView.getContext().getContentResolver().registerContentObserver(uri, true, contentObserver);
    }

    private void unsubscribeFromContentUpdates(ContentObserver contentObserver) {
        itemView.getContext().getContentResolver().unregisterContentObserver(contentObserver);
    }

    protected void setParticipants(List<User> users) {

    }
}
