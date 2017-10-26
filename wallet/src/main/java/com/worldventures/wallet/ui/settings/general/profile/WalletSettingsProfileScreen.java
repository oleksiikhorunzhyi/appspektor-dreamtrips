package com.worldventures.wallet.ui.settings.general.profile;

import com.worldventures.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.wallet.ui.settings.general.profile.common.ProfileViewModel;
import com.worldventures.wallet.ui.settings.general.profile.common.UpdateSmartCardUserView;
import com.worldventures.wallet.ui.settings.general.profile.common.WalletProfilePhotoView;

import rx.subjects.PublishSubject;

public interface WalletSettingsProfileScreen extends WalletScreen, WalletProfilePhotoView, UpdateSmartCardUserView {

   void setUser(ProfileViewModel model);

   ProfileViewModel getUser();

   void showRevertChangesDialog();

   void setDoneButtonEnabled(boolean enable);

   PublishSubject<ProfileViewModel> observeChangesProfileFields();

   void showSCNonConnectionDialog();

}
