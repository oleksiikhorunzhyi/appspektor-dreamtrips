package com.worldventures.wallet.ui.settings.general.display;

import android.support.annotation.NonNull;

import com.worldventures.wallet.domain.entity.SmartCardUser;
import com.worldventures.wallet.service.command.settings.general.display.GetDisplayTypeCommand;
import com.worldventures.wallet.service.command.settings.general.display.SaveDisplayTypeCommand;
import com.worldventures.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.wallet.ui.settings.general.profile.common.ProfileViewModel;
import com.worldventures.wallet.ui.settings.general.profile.common.UpdateSmartCardUserView;
import com.worldventures.wallet.ui.settings.general.profile.common.WalletProfilePhotoView;

import io.techery.janet.operationsubscriber.view.OperationView;
import io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction;

public interface DisplayOptionsSettingsScreen extends WalletScreen, WalletProfilePhotoView, UpdateSmartCardUserView {

   void setupViewPager(@NonNull SmartCardUser user, @SetHomeDisplayTypeAction.HomeDisplayType int type);

   OperationView<GetDisplayTypeCommand> provideGetDisplayTypeOperationView();

   OperationView<SaveDisplayTypeCommand> provideSaveDisplayTypeOperationView();

   ProfileViewModel getProfileViewModel();

   DisplayOptionsSource getDisplayOptionsSource();

   void showAddPhoneDialog();

   void updateUser(SmartCardUser user);
}
