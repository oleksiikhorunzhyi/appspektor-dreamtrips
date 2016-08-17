package com.messenger.ui.adapter.holder.chat;

import android.database.Cursor;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.messenger.entities.DataMessage;
import com.messenger.entities.DataMessage$Table;
import com.messenger.entities.DataUser;
import com.messenger.entities.DataUser$Table;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.ui.adapter.ChatCellDelegate;
import com.messenger.ui.adapter.holder.CursorViewHolder;
import com.messenger.ui.helper.MessageHelper;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

public abstract class MessageViewHolder extends CursorViewHolder {

   @InjectView(R.id.chat_date) public TextView dateTextView;

   protected String currentUserId;
   protected DataMessage dataMessage;
   protected DataUser dataUserSender;
   protected String conversationType;
   protected boolean previousMessageIsTheSameType;

   protected boolean selected;
   protected boolean isOwnMessage;
   protected boolean needMarkUnreadMessage;

   protected ChatCellDelegate cellDelegate;

   public MessageViewHolder(View itemView) {
      super(itemView);
   }

   @Override
   public void bindCursor(Cursor cursor) {
      dataMessage = SqlUtils.convertToModel(true, DataMessage.class, cursor);
      dataUserSender = convertToUserModel(cursor);
      conversationType = cursor.getString(cursor.getColumnIndex(MessageDAO.CONVERSATION_TYPE));
      previousMessageIsTheSameType = previousMessageIsTheSameType(cursor);
   }

   private DataUser convertToUserModel(Cursor cursor) {
      // we cannot use SqlUtils either for message or for sender
      // as user ID collides with message ID and is replaced by alias
      DataUser user = new DataUser(cursor.getString(cursor.getColumnIndex(DataMessage$Table.FROMID)));
      user.setFirstName(cursor.getString(cursor.getColumnIndex(DataUser$Table.FIRSTNAME)));
      user.setLastName(cursor.getString(cursor.getColumnIndex(DataUser$Table.LASTNAME)));
      user.setAvatarUrl(cursor.getString(cursor.getColumnIndex(DataUser$Table.USERAVATARURL)));
      user.setSocialId(cursor.getInt(cursor.getColumnIndex(DataUser$Table.SOCIALID)));
      return user;
   }

   /**
    * @return true if previous message is usual (not system) message and from the same user
    * or current message is system message and previous message is system message
    */
   private boolean previousMessageIsTheSameType(Cursor cursor) {
      int messageTypeIndex = cursor.getColumnIndex(DataMessage$Table.TYPE);
      int fromIdIndex = cursor.getColumnIndex(DataMessage$Table.FROMID);
      String currentMessageType = cursor.getString(messageTypeIndex);
      String currentId = cursor.getString(fromIdIndex);
      if (!cursor.moveToPrevious()) {
         cursor.moveToNext();
         return false;
      }
      String prevMessageType = cursor.getString(messageTypeIndex);
      String prevId = cursor.getString(fromIdIndex);
      boolean previousMessageIsTheSameType;
      if (MessageHelper.areDifferentUserOrSystemMessageTypes(prevMessageType, currentMessageType)) {
         previousMessageIsTheSameType = false;
      } else if (MessageHelper.isSystemMessage(prevMessageType) && MessageHelper.isSystemMessage(currentMessageType)) {
         previousMessageIsTheSameType = true;
      } else {
         previousMessageIsTheSameType = TextUtils.equals(prevId, currentId);
      }
      cursor.moveToNext();
      return previousMessageIsTheSameType;
   }

   public void setCurrentUserId(String currentUserId) {
      this.currentUserId = currentUserId;
   }

   public void setCellDelegate(ChatCellDelegate cellDelegate) {
      this.cellDelegate = cellDelegate;
   }

   public void setNeedMarkUnreadMessage(boolean needMarkUnreadMessage) {
      this.needMarkUnreadMessage = needMarkUnreadMessage;
   }

   public void setOwnMessage(boolean ownMessage) {
      isOwnMessage = ownMessage;
   }

   public void setSelected(boolean selected) {
      this.selected = selected;
   }

   public abstract View getTimestampClickableView();
}
