package com.messenger.ui.adapter.holder.conversation;

import android.database.Cursor;
import android.text.TextUtils;
import android.view.View;

import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.ui.helper.ConversationHelper;
import com.messenger.ui.helper.ConversationUIHelper;

public abstract class BaseGroupConversationViewHolder extends BaseConversationViewHolder {

   public BaseGroupConversationViewHolder(View itemView) {
      super(itemView);
   }

   @Override
   public void bindCursor(Cursor cursor) {
      super.bindCursor(cursor);

      String participantsList = null;
      int participantsCount = 0;
      if (ConversationHelper.isGroup(conversation) || ConversationHelper.isTripChat(conversation)) {
         String groupChatName = conversation.getSubject();
         if (TextUtils.isEmpty(groupChatName)) {
            participantsList = cursor.getString(cursor.getColumnIndex(ConversationsDAO.GROUP_CONVERSATION_NAME_COLUMN));
         }
         participantsCount = cursor.getInt(cursor.getColumnIndex(ConversationsDAO.GROUP_CONVERSATION_USER_COUNT_COLUMN));
      }

      String conversationName = conversation.getSubject();
      if (TextUtils.isEmpty(conversationName)) {
         conversationName = participantsList;
      }
      ConversationUIHelper.setGroupChatTitle(nameTextView, conversationName, participantsCount);
   }
}
