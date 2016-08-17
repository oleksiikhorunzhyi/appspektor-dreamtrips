package com.messenger.ui.widget.inappnotification.messanger;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataUser;
import com.messenger.notification.model.GroupNotificationData;
import com.messenger.ui.widget.GroupAvatarsView;
import com.worldventures.dreamtrips.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class InAppNotificationViewGroup extends InAppMessengerNotificationView {
   @InjectView(R.id.in_app_notif_avatar_group) GroupAvatarsView avatarViewGroup;

   public InAppNotificationViewGroup(Context context) {
      super(context);
   }

   public InAppNotificationViewGroup(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   public InAppNotificationViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
   }

   @Override
   protected void initialize() {
      ButterKnife.inject(this, LayoutInflater.from(getContext())
            .inflate(R.layout.layout_in_app_notification_group, this, true));
      super.initialize();
   }

   public void bindNotification(GroupNotificationData notification) {
      super.bindNotification(notification);
      DataConversation conversation = notification.getConversation();
      avatarViewGroup.setConversationAvatar(conversation);
      setTitle(conversation, notification.getParticipants());
   }

   protected void setTitle(DataConversation conversation, List<DataUser> participants) {
      String groupName = conversation.getSubject();
      setTitle(TextUtils.isEmpty(groupName) ? concatParticipantNames(participants) : groupName);
   }

   private String concatParticipantNames(List<DataUser> participants) {
      return Queryable.from(participants).map(DataUser::getName).joinStrings(", ");
   }

   @Override
   protected String getMessageText(DataUser sender, DataMessage dataMessage, @Nullable String attachmentType) {
      return String.format("%s: %s", sender.getName(), super.getMessageText(sender, dataMessage, attachmentType));
   }
}
