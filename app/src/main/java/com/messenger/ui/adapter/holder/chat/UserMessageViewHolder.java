package com.messenger.ui.adapter.holder.chat;

import android.database.Cursor;
import android.text.TextUtils;
import android.view.View;

import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataTranslation;
import com.messenger.messengerservers.constant.ConversationType;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.ui.adapter.inflater.UserMessageCommonInflater;
import com.messenger.ui.adapter.inflater.UserMessageHolderInflater;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.Optional;

public class UserMessageViewHolder extends MessageViewHolder {

   protected static final float ALPHA_MESSAGE_SENDING = 0.5f;
   protected static final float ALPHA_MESSAGE_NORMAL = 1f;

   protected DataAttachment dataAttachment;
   protected DataTranslation dataTranslation;

   @Optional @InjectView(R.id.view_retry_send) public View viewRetrySend;
   @InjectView(R.id.message_container) public View messageContainer;

   protected boolean isGroupMessage;

   private final UserMessageCommonInflater messageCommonInflater;
   private final UserMessageHolderInflater userMessageHolderInflater;

   public UserMessageViewHolder(View itemView) {
      super(itemView);
      messageCommonInflater = new UserMessageCommonInflater(itemView);
      userMessageHolderInflater = new UserMessageHolderInflater(itemView);
   }

   @Override
   public void bindCursor(Cursor cursor) {
      super.bindCursor(cursor);
      dataAttachment = SqlUtils.convertToModel(true, DataAttachment.class, cursor);
      boolean translationExist = !TextUtils.isEmpty(cursor.getString(cursor.getColumnIndex(MessageDAO.TRANSLATION_ID)));
      dataTranslation = translationExist ? SqlUtils.convertToModel(true, DataTranslation.class, cursor) : null;
      isGroupMessage = !TextUtils.equals(conversationType, ConversationType.CHAT);
      //
      messageCommonInflater.onCellBind(previousMessageIsTheSameType, shouldMarkAsUnread() && isUnread(), selected);
      userMessageHolderInflater.onCellBind(dataUserSender, isGroupMessage, previousMessageIsTheSameType);
   }

   @Optional
   @OnLongClick(R.id.message_container)
   boolean onMessageLongClicked() {
      cellDelegate.onMessageLongClicked(dataMessage);
      return true;
   }

   @Optional
   @OnClick(R.id.view_retry_send)
   void onRetry() {
      cellDelegate.onRetryClicked(dataMessage);
   }

   @Optional
   @OnClick(R.id.chat_item_avatar)
   void onUserAvatarClicked() {
      cellDelegate.onAvatarClicked(dataUserSender);
   }

   protected boolean isUnread() {
      return dataMessage.getStatus() == MessageStatus.SENT;
   }

   protected boolean shouldMarkAsUnread() {
      // server always keeps SENT status for our own messages,
      // make sure we don't show our own messages as unread
      return !isOwnMessage && needMarkUnreadMessage;
   }

   @Override
   public View getTimestampClickableView() {
      return messageContainer;
   }
}
