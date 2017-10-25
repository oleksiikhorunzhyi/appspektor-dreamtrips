package com.messenger.ui.view.settings;

import android.support.annotation.StringRes;

import com.messenger.entities.DataUser;

import rx.functions.Action0;

public interface GroupChatSettingsScreen extends ChatSettingsScreen {
   void openPicker();

   void showChangingAvatarProgressBar();

   void hideChangingAvatarProgressBar();

   void setLeaveButtonVisible(boolean visible);

   void setOwner(DataUser owner);

   void showSubjectDialog(String currentSubject);

   void showLeaveChatDialog(String currentSubject);

   void showMessage(@StringRes int text, Action0 action);
}
