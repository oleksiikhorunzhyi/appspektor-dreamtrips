package com.messenger.ui.presenter.settings;

import com.messenger.ui.view.settings.GroupChatSettingsScreen;
import com.worldventures.core.modules.picker.model.PhotoPickerModel;

public interface GroupChatSettingsScreenPresenter extends ChatSettingsScreenPresenter<GroupChatSettingsScreen> {

   void onImagePicked(PhotoPickerModel photoPickerModel);

   void onLeaveChatClicked();

   void onMembersRowClicked();

   void applyNewChatSubject(String subject);

   void onLeaveButtonClick();

   void recheckPermission(String[] permissions, boolean userAnswer);
}
