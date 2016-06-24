package com.messenger.ui.presenter.settings;

import com.messenger.ui.presenter.MessengerPresenter;
import com.messenger.ui.view.settings.ChatSettingsScreen;
import com.messenger.ui.viewstate.ChatSettingsViewState;

public interface ChatSettingsScreenPresenter<C extends ChatSettingsScreen> extends MessengerPresenter<C,
        ChatSettingsViewState> {

    void onClearChatHistoryClicked();

    void onClearChatHistory();

    void onNotificationsSwitchClicked(boolean isChecked);
}

