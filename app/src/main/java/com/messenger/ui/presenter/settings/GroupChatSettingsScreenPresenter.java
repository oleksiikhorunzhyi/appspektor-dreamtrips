package com.messenger.ui.presenter.settings;

import com.messenger.ui.view.settings.GroupChatSettingsScreen;
import com.worldventures.dreamtrips.modules.media_picker.model.PhotoPickerModel;

public interface GroupChatSettingsScreenPresenter extends ChatSettingsScreenPresenter<GroupChatSettingsScreen> {

   void onImagePicked(PhotoPickerModel photoPickerModel);

   void onLeaveChatClicked();

   void onMembersRowClicked();

   void applyNewChatSubject(String subject);

   void onLeaveButtonClick();
}
