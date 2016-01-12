package com.messenger.ui.adapter.holder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.messenger.messengerservers.entities.User;
import com.messenger.storage.dao.ParticipantsDAO;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.techery.spares.module.Injector;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class BaseConversationViewHolder extends BaseViewHolder {

    @InjectView(R.id.conversation_item_view)
    ViewGroup contentLayout;
    @InjectView(R.id.conversation_name_textview)
    TextView nameTextView;
    @InjectView(R.id.conversation_last_message_textview)
    TextView lastMessageTextView;
    @InjectView(R.id.conversation_last_message_date_textview)
    TextView lastMessageDateTextView;
    @InjectView(R.id.conversation_unread_messages_count_textview)
    TextView unreadMessagesCountTextView;
    //
    @InjectView(R.id.swipe)
    SwipeLayout swipeLayout;
    @InjectView(R.id.swipe_layout_button_delete)
    View deleteButton;
    @InjectView(R.id.swipe_layout_button_more)
    View moreButton;
    //

    @Inject
    ParticipantsDAO participantsDAO;

    private Subscription participantsSubscriber;

    public BaseConversationViewHolder(View itemView) {
        super(itemView);
        ((Injector) itemView.getContext().getApplicationContext()).inject(this);
    }

    public ViewGroup getContentLayout() {
        return contentLayout;
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

    public void updateParticipants(String conversationId, Action1<List<User>> listener) {
        if (participantsSubscriber != null && !participantsSubscriber.isUnsubscribed()) {
            participantsSubscriber.unsubscribe();
        }

        participantsSubscriber = participantsDAO.getParticipants(conversationId)
                .map(cursor -> {
                    List<User> result = SqlUtils.convertToList(User.class, cursor);
                    cursor.close();
                    return result;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycle.bindView(itemView))
                .onErrorReturn(throwable -> {
                    Timber.e(throwable, "Load participants on ");
                    return Collections.<User>emptyList();
                })
                .subscribe(listener::call);
    }

}
