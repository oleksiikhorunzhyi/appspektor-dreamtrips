package com.messenger.ui.adapter.holder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataTranslation;
import com.messenger.entities.DataUser;
import com.messenger.ui.adapter.ConversationsCursorAdapter;
import com.messenger.ui.adapter.inflater.ConversationLastMessageDateInflater;
import com.messenger.ui.adapter.inflater.ConversationLastMessageInflater;
import com.messenger.ui.adapter.inflater.ConversationSwipeLayoutInflater;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;

import javax.inject.Inject;

import butterknife.InjectView;

public abstract class BaseConversationViewHolder extends BaseViewHolder
     implements View.OnClickListener {

    protected final Context context;

    @InjectView(R.id.conversation_item_view)
    ViewGroup contentLayout;
    @InjectView(R.id.conversation_name_textview)
    TextView nameTextView;
    @InjectView(R.id.conversation_unread_messages_count_textview)
    TextView unreadMessagesCountTextView;

    private ConversationsCursorAdapter.ConversationClickListener conversationClickListener;

    private ConversationLastMessageInflater lastMessageInflater = new ConversationLastMessageInflater();
    private ConversationLastMessageDateInflater lastMessageDateInflater = new ConversationLastMessageDateInflater();
    private ConversationSwipeLayoutInflater conversationSwipeLayoutInflater = new ConversationSwipeLayoutInflater() ;

    @Inject
    DataUser currentUser;
    private DataConversation conversation;

    public BaseConversationViewHolder(View itemView) {
        super(itemView);
        context = itemView.getContext();
        itemView.setOnClickListener(this);
        ((Injector) itemView.getContext().getApplicationContext()).inject(this);
        lastMessageInflater.setView(itemView);
        lastMessageDateInflater.setView(itemView);
        conversationSwipeLayoutInflater.setView(itemView, this);
    }

    public void bindConversation(DataConversation conversation, String participantsList, int participantsCount) {
        this.conversation = conversation;
        updateUnreadCountTextView();
        conversationSwipeLayoutInflater.bind(conversation, currentUser);
    }

    protected void updateUnreadCountTextView() {
        int unreadMessageCount = conversation.getUnreadMessageCount();
        final boolean hasNewMessage = unreadMessageCount > 0;
        unreadMessagesCountTextView.setVisibility(hasNewMessage ? View.VISIBLE : View.GONE);
        unreadMessagesCountTextView.setText(hasNewMessage ? String.valueOf(unreadMessageCount) : null);
    }

    public void bindLastMessage(DataMessage message, String messageAuthor,
                                String attachmentType, DataTranslation translation) {
        lastMessageInflater.setLastMessage(conversation, message, messageAuthor, currentUser,
                attachmentType, translation);
        lastMessageDateInflater.setDate(conversation);
    }

    public void setConversationClickListener(ConversationsCursorAdapter.ConversationClickListener conversationClickListener) {
        this.conversationClickListener = conversationClickListener;
    }

    public void setSwipeButtonsListener(ConversationsCursorAdapter.SwipeButtonsListener swipeButtonsListener) {
        conversationSwipeLayoutInflater.setSwipeButtonsListener(swipeButtonsListener);
    }

    @Override
    public void onClick(View view) {
        if (conversationClickListener != null) conversationClickListener.onConversationClick(conversation);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Selection
    ///////////////////////////////////////////////////////////////////////////

    public void applySelection(String selectedConversationId) {
        if (conversation.getId().equals(selectedConversationId)) {
            contentLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.conversation_list_selected_conversation_bg));
        } else if (conversation.getUnreadMessageCount() > 0) {
            contentLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.conversation_list_unread_conversation_bg));
        } else {
            contentLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.conversation_list_read_conversation_bg));
        }
    }
}
