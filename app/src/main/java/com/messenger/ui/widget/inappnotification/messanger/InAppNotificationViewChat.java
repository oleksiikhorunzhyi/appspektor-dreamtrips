package com.messenger.ui.widget.inappnotification.messanger;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.messenger.entities.DataUser;
import com.messenger.notification.model.SingleChatNotificationData;
import com.messenger.ui.widget.AvatarView;
import com.worldventures.dreamtrips.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class InAppNotificationViewChat extends InAppMessengerNotificationView {
   @InjectView(R.id.in_app_notif_avatar) AvatarView avatarView;

   public InAppNotificationViewChat(Context context) {
      super(context);
   }

   public InAppNotificationViewChat(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   public InAppNotificationViewChat(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
   }

   @Override
   protected void initialize() {
      ButterKnife.inject(this, LayoutInflater.from(getContext())
            .inflate(R.layout.layout_in_app_notification_chat, this, true));
      super.initialize();
   }

   public void bindNotification(SingleChatNotificationData notification) {
      super.bindNotification(notification);
      DataUser sender = notification.getSender();
      setAvatarUrl(sender.getAvatarUrl());
      setTitle(sender.getDisplayedName());
   }

   protected void setAvatarUrl(@Nullable String avatarUrl) {
      avatarView.setImageURI(TextUtils.isEmpty(avatarUrl) ? Uri.EMPTY : Uri.parse(avatarUrl));
   }
}
