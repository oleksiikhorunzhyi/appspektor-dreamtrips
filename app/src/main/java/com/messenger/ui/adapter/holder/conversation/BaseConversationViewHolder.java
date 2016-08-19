package com.messenger.ui.adapter.holder.conversation;

import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataTranslation;
import com.messenger.entities.DataUser;
import com.messenger.ui.adapter.ConversationsCursorAdapter;
import com.messenger.ui.adapter.converter.ConversationListDataConverter;
import com.messenger.ui.adapter.holder.BaseViewHolder;
import com.messenger.ui.adapter.inflater.conversation.ConversationLastMessageDateInflater;
import com.messenger.ui.adapter.inflater.conversation.ConversationSwipeLayoutInflater;
import com.messenger.ui.adapter.inflater.conversation.LastMessageTextProvider;
import com.techery.spares.module.Injector;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.session.UserSession;

import javax.inject.Inject;

import butterknife.InjectView;

public abstract class BaseConversationViewHolder extends BaseViewHolder
      implements View.OnClickListener {

   @Inject SessionHolder<UserSession> sessionHolder;
   protected DataUser currentUser;
   protected DataConversation conversation;
   protected DataMessage message;
   protected String attachmentType;
   protected DataTranslation translation;
   protected DataUser sender;
   protected DataUser recipient;

   @InjectView(R.id.conversation_item_view) ViewGroup contentLayout;
   @InjectView(R.id.conversation_name_textview) TextView nameTextView;
   @InjectView(R.id.conversation_last_message_textview) TextView lastMessageTextView;
   @InjectView(R.id.conversation_unread_messages_count_textview)
   TextView unreadMessagesCountTextView;

   private ConversationsCursorAdapter.ConversationClickListener conversationClickListener;

   private ConversationLastMessageDateInflater lastMessageDateInflater = new ConversationLastMessageDateInflater();
   private ConversationSwipeLayoutInflater conversationSwipeLayoutInflater = new ConversationSwipeLayoutInflater();
   private ConversationListDataConverter converter = new ConversationListDataConverter();
   private LastMessageTextProvider lastMessageTextProvider;

   public BaseConversationViewHolder(View itemView) {
      super(itemView);
      context = itemView.getContext();
      itemView.setOnClickListener(this);
      ((Injector) context.getApplicationContext()).inject(this);
      currentUser = new DataUser(sessionHolder.get().get().getUsername());
      lastMessageTextProvider = new LastMessageTextProvider(context, currentUser);
      lastMessageDateInflater.setView(itemView);
      conversationSwipeLayoutInflater.setView(itemView, this);
   }

   public void bindCursor(Cursor cursor) {
      ConversationListDataConverter.Result result = converter.convert(cursor);
      conversation = result.getConversation();
      message = result.getMessage();
      translation = result.getTranslation();
      sender = result.getSender();
      recipient = result.getRecipient();
      attachmentType = result.getAttachmentType();
      bindConversation();
      bindLastMessage();
   }

   private void bindConversation() {
      updateUnreadCountTextView();
      conversationSwipeLayoutInflater.bind(conversation, currentUser);
   }

   protected void updateUnreadCountTextView() {
      int unreadMessageCount = conversation.getUnreadMessageCount();
      final boolean hasNewMessage = unreadMessageCount > 0;
      unreadMessagesCountTextView.setVisibility(hasNewMessage ? View.VISIBLE : View.GONE);
      unreadMessagesCountTextView.setText(hasNewMessage ? String.valueOf(unreadMessageCount) : null);
   }

   private void bindLastMessage() {
      String lastMessageText = lastMessageTextProvider.getLastMessageText(conversation, message, sender,
            recipient, attachmentType, translation);
      lastMessageTextView.setText(lastMessageText);
      updateLastMessageDateTextView();
   }

   protected void updateLastMessageDateTextView() {
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