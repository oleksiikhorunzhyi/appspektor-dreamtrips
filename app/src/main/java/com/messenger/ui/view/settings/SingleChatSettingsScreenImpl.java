package com.messenger.ui.view.settings;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.messenger.entities.DataConversation;
import com.messenger.entities.DataUser;
import com.messenger.ui.presenter.settings.SingleChatSettingsScreenPresenter;
import com.messenger.ui.presenter.settings.SingleChatSettingsScreenPresenterImpl;
import com.worldventures.dreamtrips.R;

import java.util.List;

import butterknife.OnClick;

public class SingleChatSettingsScreenImpl extends BaseChatSettingsScreen<ChatSettingsScreen, SingleChatSettingsScreenPresenter, SingleSettingsPath> {

   public SingleChatSettingsScreenImpl(Context context) {
      super(context);
   }

   public SingleChatSettingsScreenImpl(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @OnClick(R.id.chat_settings_single_chat_avatar_view)
   public void onAvatarClick() {
      getPresenter().onConversationAvatarClick();
   }

   @Override
   public void setConversation(@NonNull DataConversation conversation) {
      super.setConversation(conversation);
      toolbarPresenter.setTitle(R.string.chat_settings_single_chat);
   }

   @Override
   public void setParticipants(DataConversation conversation, List<DataUser> participants) {
      if (participants.isEmpty()) return;
      //
      singleChatAvatarView.setVisibility(VISIBLE);
      DataUser addressee = participants.get(0);
      singleChatAvatarView.setImageURI(Uri.parse(addressee.getAvatarUrl()));
      chatNameTextView.setText(addressee.getName());
      chatDescriptionTextView.setText(addressee.isOnline() ? R.string.chat_settings_single_chat_online : R.string.chat_settings_single_chat_offline);
   }

   @NonNull
   @Override
   public SingleChatSettingsScreenPresenter createPresenter() {
      return new SingleChatSettingsScreenPresenterImpl(getContext(), injector, getPath().getConversationId());
   }
}
