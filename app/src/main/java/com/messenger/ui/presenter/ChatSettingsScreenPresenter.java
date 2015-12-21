package com.messenger.ui.presenter;

import com.messenger.ui.view.ChatSettingsScreen;
import com.messenger.ui.viewstate.ChatSettingsViewState;

public interface ChatSettingsScreenPresenter extends ActivityAwareViewStateMvpPresenter<ChatSettingsScreen,
        ChatSettingsViewState> {
    void onClearChatHistoryClicked();
    void onLeaveChatClicked();
    void onNotificationsSwitchClicked(boolean isChecked);
    void onMembersRowClicked();
}

