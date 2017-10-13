package com.worldventures.dreamtrips.wallet.ui.settings.general.profile;

import com.worldventures.dreamtrips.social.ui.util.PermissionUIComponent;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.ProfileViewModel;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.UpdateSmartCardUserView;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletProfilePhotoView;

import rx.subjects.PublishSubject;

public interface WalletSettingsProfileScreen extends WalletScreen, WalletProfilePhotoView, UpdateSmartCardUserView,
      PermissionUIComponent {

   void setUser(ProfileViewModel model);

   ProfileViewModel getUser();

   void showRevertChangesDialog();

   void setDoneButtonEnabled(boolean enable);

   PublishSubject<ProfileViewModel> observeChangesProfileFields();

   void showSCNonConnectionDialog();

}
