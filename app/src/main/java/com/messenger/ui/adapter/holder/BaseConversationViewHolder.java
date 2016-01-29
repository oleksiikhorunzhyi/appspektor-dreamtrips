package com.messenger.ui.adapter.holder;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.User;
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
    User user;

    protected final ConversationHelper conversationHelper = new ConversationHelper();
    protected final View.OnClickListener onClickListener = this::onClick;
    protected final SwipeClickListener swipeClickListener;
    protected final Context context;

    private ConversationsCursorAdapter.SwipeButtonsListener swipeButtonsListener;
    private ConversationsCursorAdapter.ConversationClickListener conversationClickListener;
    private Subscription participantsSubscriber;
    private Conversation conversation;
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

    public void bindConversation(@NonNull Conversation conversation, String selectedConversationId) {
        this.conversation = conversation;
        loadParticipants(conversation);
        setUnreadMessageCount(conversation.getUnreadMessageCount());
        applySelection(selectedConversationId);
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

    public void setDate(String date) {
        lastMessageDateTextView.setText(date);
    }


    public void setLastMessage(String message) {
        lastMessageTextView.setText(message);
    }

    protected void setUnreadMessageCount(int unreadMessageCount) {
        final boolean hasNewMessage = unreadMessageCount > 0;
        final int countMessageVisible = hasNewMessage ? View.VISIBLE : View.GONE;

        if (countMessageVisible != unreadMessagesCountTextView.getVisibility()) {
            setUnreadProperties(countMessageVisible,
                    hasNewMessage ? R.color.conversation_list_unread_conversation_bg : R.color.conversation_list_read_conversation_bg);
        }

        unreadMessagesCountTextView.setText(hasNewMessage ? String.valueOf(unreadMessageCount) : null);
    }

    private void setUnreadProperties(int visible, @ColorRes int backgroundProperties) {
        unreadMessagesCountTextView.setVisibility(visible);
        itemView.setBackgroundColor(ContextCompat.getColor(context, backgroundProperties));
    }

    public void setDeleteButtonVisibility(boolean visible) {
        int viewVisible = visible ? View.VISIBLE : View.GONE;
        if (deleteButton.getVisibility() != viewVisible) deleteButton.setVisibility(viewVisible);
    }

    protected void onParticipantsLoaded(Conversation conversation, List<User> participants) {
        if (participants == null || participants.size() == 0) return;

        conversationHelper.setTitle(nameTextView, conversation, participants, true);
        setConversationPicture(participants);
    }

    protected abstract void setConversationPicture(List<User> participants);

    private void loadParticipants(final Conversation conversation) {
        if (participantsSubscriber != null && !participantsSubscriber.isUnsubscribed()) {
            participantsSubscriber.unsubscribe();
        }

        Observable<List<User>> participantsObservable;
        if (TextUtils.equals(conversation.getType(), Conversation.Type.CHAT)) {
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
                .onErrorReturn(throwable -> Collections.<User>emptyList())
                .subscribe(users -> {
                    final Runnable runnable = () -> onParticipantsLoaded(conversation, users);
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

    private void applySelection(String conversationId) {
        if (!conversation.getId().equals(conversationId)) {
            contentLayout.setBackgroundColor(context.getResources().getColor(R.color.conversation_list_read_conversation_bg));
        } else {
            contentLayout.setBackgroundColor(context.getResources().getColor(R.color.conversation_list_selected_conversation_bg));
        }
    }

}
