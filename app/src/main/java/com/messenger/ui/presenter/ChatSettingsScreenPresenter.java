package com.messenger.ui.presenter;

import com.messenger.ui.view.settings.ChatSettingsScreen;
import com.messenger.ui.viewstate.ChatSettingsViewState;

public interface ChatSettingsScreenPresenter<C extends ChatSettingsScreen> extends MessengerPresenter<C,
        ChatSettingsViewState> {

    void onClearChatHistoryClicked();

    void onLeaveChatClicked();

    void onNotificationsSwitchClicked(boolean isChecked);

    void onMembersRowClicked();

    void onConversationAvatarClick();

    void applyNewChatSubject(String subject);

    void onLeaveButtonClick();
}

