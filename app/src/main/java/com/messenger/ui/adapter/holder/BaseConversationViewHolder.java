package com.messenger.ui.adapter.holder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.messenger.entities.DataConversation;
import com.messenger.ui.adapter.ConversationsCursorAdapter;
import com.messenger.util.SwipeClickListener;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

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

    protected final View.OnClickListener onClickListener = this::onClick;
    protected final SwipeClickListener swipeClickListener;
    protected final Context context;

    private ConversationsCursorAdapter.SwipeButtonsListener swipeButtonsListener;
    private ConversationsCursorAdapter.ConversationClickListener conversationClickListener;
    private DataConversation conversation;

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
        //
        int unreadMessageCount = conversation.getUnreadMessageCount();
        final boolean hasNewMessage = unreadMessageCount > 0;

        unreadMessagesCountTextView.setVisibility(hasNewMessage ? View.VISIBLE : View.GONE);
        unreadMessagesCountTextView.setText(hasNewMessage ? String.valueOf(unreadMessageCount) : null);
        setConversationId(conversation.getId());
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
