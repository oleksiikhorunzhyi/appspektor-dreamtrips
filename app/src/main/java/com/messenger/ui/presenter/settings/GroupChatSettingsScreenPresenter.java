package com.messenger.ui.presenter.settings;

import com.messenger.ui.view.settings.GroupChatSettingsScreen;

public interface GroupChatSettingsScreenPresenter extends ChatSettingsScreenPresenter<GroupChatSettingsScreen> {

    void onLeaveChatClicked();

    void onMembersRowClicked();

    void applyNewChatSubject(String subject);

    void onLeaveButtonClick();
}
