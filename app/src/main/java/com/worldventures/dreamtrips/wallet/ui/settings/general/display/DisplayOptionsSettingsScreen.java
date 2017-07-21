package com.worldventures.dreamtrips.wallet.ui.settings.general.display;

import android.support.annotation.NonNull;

import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.service.command.settings.general.display.GetDisplayTypeCommand;
import com.worldventures.dreamtrips.wallet.service.command.settings.general.display.SaveDisplayTypeCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

import io.techery.janet.operationsubscriber.view.OperationView;
import io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction;

public interface DisplayOptionsSettingsScreen extends WalletScreen{

   void setupViewPager(@NonNull SmartCardUser user, @SetHomeDisplayTypeAction.HomeDisplayType int type);

   OperationView<GetDisplayTypeCommand> provideGetDisplayTypeOperationView();

   OperationView<SaveDisplayTypeCommand> provideSaveDisplayTypeOperationView();

   SmartCardUser getSmartCardUser();

   DisplayOptionsSource getDisplayOptionsSource();
}
