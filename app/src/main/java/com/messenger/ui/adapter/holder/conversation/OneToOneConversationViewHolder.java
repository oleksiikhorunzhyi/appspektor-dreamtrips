package com.messenger.ui.adapter.holder.conversation;

import android.database.Cursor;
import android.net.Uri;
import android.view.View;

import com.messenger.entities.DataUser$Table;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.ui.widget.AvatarView;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

public class OneToOneConversationViewHolder extends BaseConversationViewHolder {

   @InjectView(R.id.conversation_avatar_view) AvatarView avatarView;

   public OneToOneConversationViewHolder(View itemView) {
      super(itemView);
   }

   @Override
   public void bindCursor(Cursor cursor) {
      super.bindCursor(cursor);
      String avatarUrl = cursor.getString(cursor.getColumnIndex(DataUser$Table.USERAVATARURL));
      // Database does not have boolean type and store true as 1, false as 0
      boolean online = cursor.getInt(cursor.getColumnIndex(DataUser$Table.ONLINE)) == 1;
      String username = cursor.getString(cursor.getColumnIndex(ConversationsDAO.SINGLE_CONVERSATION_NAME_COLUMN));
      avatarView.setOnline(online);
      avatarView.setImageURI(avatarUrl != null ? Uri.parse(avatarUrl) : null);
      nameTextView.setText(username);
   }
}
