package com.worldventures.dreamtrips.wallet.ui.settings.general.display;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterI;

import io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction;

public interface DisplayOptionsSettingsPresenter extends WalletPresenterI<DisplayOptionsSettingsScreen> {

   void goBack();

   void saveDisplayType(@SetHomeDisplayTypeAction.HomeDisplayType int type);

   void openEditProfileScreen();

   void fetchDisplayType();

}