package com.messenger.ui.adapter.holder;

import android.view.View;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.messenger.messengerservers.entities.ParticipantsRelationship;
import com.messenger.messengerservers.entities.User;
import com.messenger.util.RxContentResolver;
import com.messenger.util.RxContentResolver.Query;
import com.messenger.util.RxContentResolver.Query.Builder;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;

import java.util.Collections;
import java.util.List;

import butterknife.InjectView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
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
    @InjectView(R.id.conversation_abandoned_status)
    View abandonedView;
    //
    @InjectView(R.id.swipe)
    SwipeLayout swipeLayout;
    @InjectView(R.id.swipe_layout_button_delete)
    View deleteButton;
    @InjectView(R.id.swipe_layout_button_more)
    View moreButton;
    //
    private final RxContentResolver contentResolver;
    private Subscription participantsSubscriber;

    public BaseConversationViewHolder(View itemView) {
        super(itemView);
        contentResolver = new RxContentResolver(itemView.getContext().getContentResolver(),
                query -> FlowManager.getDatabaseForTable(User.class).getWritableDatabase()
                .rawQuery(query.selection, query.selectionArgs));
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

    public SwipeLayout getSwipeLayout() {
        return swipeLayout;
    }

    public View getDeleteButton() {
        return deleteButton;
    }

    public View getMoreButton() {
        return moreButton;
    }

    public View getAbandonedView() {
        return abandonedView;
    }

    public void updateParticipants(String conversationId, Action1<List<User>> listener) {
        if (participantsSubscriber != null && !participantsSubscriber.isUnsubscribed()) {
            participantsSubscriber.unsubscribe();
        }
        Query q = new Builder(null)
                .withSelection("SELECT * FROM Users u " +
                        "JOIN ParticipantsRelationship p " +
                        "ON p.userId = u._id " +
                        "WHERE p.conversationId = ?"
                ).withSelectionArgs(new String[]{conversationId}).build();
        participantsSubscriber = contentResolver.query(q, User.CONTENT_URI, ParticipantsRelationship.CONTENT_URI)
                .onBackpressureLatest()
                .map(c -> SqlUtils.convertToList(User.class, c))
                .onErrorReturn(throwable -> Collections.<User>emptyList())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycle.bindView(itemView))
                .subscribe(users -> {
                    listener.call(users);
                });
    }

}
