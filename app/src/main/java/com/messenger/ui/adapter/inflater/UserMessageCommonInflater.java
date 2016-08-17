package com.messenger.ui.adapter.inflater;

import android.view.View;

import com.messenger.ui.widget.ChatItemFrameLayout;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

public class UserMessageCommonInflater extends MessageCommonInflater {

   @InjectView(R.id.chat_message_container) public View chatMessageContainer;
   @InjectView(R.id.message_container) public ChatItemFrameLayout messageContainer;

   public UserMessageCommonInflater(View itemView) {
      super(itemView);
   }

   public void onCellBind(boolean previousMessageIsTheSameType, boolean unread, boolean selected) {
      super.onCellBind(previousMessageIsTheSameType);
      updateUnreadStatus(unread);
      messageContainer.setSelected(selected);
      messageContainer.setPreviousMessageFromSameUser(previousMessageIsTheSameType);
   }

   public void updateUnreadStatus(boolean unread) {
      chatMessageContainer.setBackgroundResource(unread ? R.color.chat_list_item_read_unread_background : R.color.chat_list_item_read_read_background);
   }
}
