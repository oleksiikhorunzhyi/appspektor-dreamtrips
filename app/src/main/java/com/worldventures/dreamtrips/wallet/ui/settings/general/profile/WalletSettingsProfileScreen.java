package com.worldventures.dreamtrips.wallet.ui.settings.general.profile;

import com.worldventures.dreamtrips.wallet.service.command.profile.RetryHttpUploadUpdatingCommand;
import com.worldventures.dreamtrips.wallet.service.command.profile.UpdateSmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.ProfileViewModel;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletProfilePhotoView;

import io.techery.janet.operationsubscriber.view.OperationView;
import rx.subjects.PublishSubject;

public interface WalletSettingsProfileScreen extends WalletScreen, WalletProfilePhotoView {

   void setUser(ProfileViewModel model);

   ProfileViewModel getUser();

   void showRevertChangesDialog();

   OperationView<UpdateSmartCardUserCommand> provideUpdateSmartCardOperation();

   OperationView<RetryHttpUploadUpdatingCommand> provideHttpUploadOperation();

   void setDoneButtonEnabled(boolean enable);

   PublishSubject<ProfileViewModel> observeChangesProfileFields();

   void showSCNonConnectionDialog();

}
