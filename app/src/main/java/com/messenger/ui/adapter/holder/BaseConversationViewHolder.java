package com.messenger.ui.adapter.holder;

import android.view.View;
import android.widget.TextView;

import com.innahema.collections.query.functions.Action1;
import com.messenger.messengerservers.entities.User;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;

import java.util.List;

import butterknife.InjectView;
import rx.Observable;
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
            return Observable.just(SqlUtils.queryList(User.class, query, conversationId));
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycle.bindView(itemView))
                .subscribe(users -> {
                    setParticipants(users);
                    listener.apply(users);
                });
    }

    protected void setParticipants(List<User> users) {

    }
}
