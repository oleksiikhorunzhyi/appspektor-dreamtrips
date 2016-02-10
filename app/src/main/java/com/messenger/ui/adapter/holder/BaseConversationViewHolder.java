package com.messenger.ui.adapter.holder;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.constant.ConversationType;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.ui.adapter.ConversationsCursorAdapter;
import com.messenger.ui.helper.ConversationHelper;
import com.messenger.util.SwipeClickListener;
import com.techery.spares.module.Injector;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.composer.NonNullFilter;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

public abstract class BaseConversationViewHolder extends BaseViewHolder {

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
    @Inject
    DataUser user;

    protected final ConversationHelper conversationHelper = new ConversationHelper();
    protected final View.OnClickListener onClickListener = this::onClick;
    protected final SwipeClickListener swipeClickListener;
    protected final Context context;

    private ConversationsCursorAdapter.SwipeButtonsListener swipeButtonsListener;
    private ConversationsCursorAdapter.ConversationClickListener conversationClickListener;
    private Subscription participantsSubscriber;
    private DataConversation conversation;
    private Handler handler = new Handler();

    public BaseConversationViewHolder(View itemView) {
        super(itemView);
        context = itemView.getContext();
        swipeClickListener = new SwipeClickListener(itemView, onClickListener);
        swipeLayout.addSwipeListener(swipeClickListener);
        itemView.setOnClickListener(onClickListener);
        deleteButton.setOnClickListener(onClickListener);
        moreButton.setOnClickListener(onClickListener);
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

    public void bindConversation(@NonNull DataConversation conversation, String selectedConversationId) {
        this.conversation = conversation;
        loadParticipants(conversation);
        //
        int unreadMessageCount = conversation.getUnreadMessageCount();
        final boolean hasNewMessage = unreadMessageCount > 0;

        unreadMessagesCountTextView.setVisibility(hasNewMessage ? View.VISIBLE : View.GONE);
        unreadMessagesCountTextView.setText(hasNewMessage ? String.valueOf(unreadMessageCount) : null);

        applySelection(selectedConversationId, hasNewMessage);
    }

    protected void onClickDeleteButton() {
        if (swipeButtonsListener != null) swipeButtonsListener.onDeleteButtonPressed(conversation);
    }

    protected void onClickMoreButton() {
        if (swipeButtonsListener != null) swipeButtonsListener.onMoreOptionsButtonPressed(conversation);
    }

    protected void onItemClick() {
        if (conversationClickListener != null)
            conversationClickListener.onConversationClick(conversation);
    }

    protected void onClick(View view) {
        switch (view.getId()) {
            case R.id.swipe_layout_button_more:
                onClickMoreButton();
                break;
            case R.id.swipe_layout_button_delete:
                onClickDeleteButton();
                break;
            default:
                onItemClick();
        }
    }


    protected void setConversationWithParticipants(DataConversation conversation, List<DataUser> participants) {
        if (participants == null || participants.size() == 0) return;

        conversationHelper.setTitle(nameTextView, conversation, participants, true);
        setConversationId(conversation.getId());
        setParticipants(participants);
    }

    public void setLastMessage(String message) {
        lastMessageTextView.setText(message);
    }

    public void setDate(String date) {
        lastMessageDateTextView.setText(date);
    }

    public void setDeleteButtonVisibility(boolean visible) {
        int viewVisible = visible ? View.VISIBLE : View.GONE;
        if (deleteButton.getVisibility() != viewVisible) deleteButton.setVisibility(viewVisible);
    }

    protected abstract void setConversationId(String conversationId);

    protected abstract void setParticipants(List<DataUser> participants);

    private void loadParticipants(final DataConversation conversation) {
        if (participantsSubscriber != null && !participantsSubscriber.isUnsubscribed()) {
            participantsSubscriber.unsubscribe();
        }

        Observable<List<DataUser>> participantsObservable;
        if (TextUtils.equals(conversation.getType(), ConversationType.CHAT)) {
            participantsObservable = participantsDAO.getParticipant(conversation.getId(), user.getId())
                    .compose(new NonNullFilter<>())
                    .map(Collections::singletonList);
        } else {
            participantsObservable = participantsDAO.getParticipantsEntities(conversation.getId());
        }
        participantsSubscriber = participantsObservable
                .onBackpressureLatest()
                .subscribeOn(Schedulers.immediate())
                .compose(RxLifecycle.bindView(itemView))
                .onErrorReturn(throwable -> Collections.<DataUser>emptyList())
                .subscribe(users -> {
                    final Runnable runnable = () -> setConversationWithParticipants(conversation, users);
                    if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
                        handler.post(runnable);
                    } else {
                        runnable.run();
                    }
                });
    }

    public void setConversationClickListener(ConversationsCursorAdapter.ConversationClickListener conversationClickListener) {
        this.conversationClickListener = conversationClickListener;
    }

    public void setSwipeButtonsListener(ConversationsCursorAdapter.SwipeButtonsListener swipeButtonsListener) {
        this.swipeButtonsListener = swipeButtonsListener;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Selection
    ///////////////////////////////////////////////////////////////////////////

    private void applySelection(String conversationId, boolean unread) {
        if (conversation.getId().equals(conversationId)) {
            contentLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.conversation_list_selected_conversation_bg));
        } else if (unread) {
            contentLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.conversation_list_unread_conversation_bg));
        } else {
            contentLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.conversation_list_read_conversation_bg));
        }
    }

}
