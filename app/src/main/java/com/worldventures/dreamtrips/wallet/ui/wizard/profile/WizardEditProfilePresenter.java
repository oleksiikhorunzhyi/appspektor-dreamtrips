package com.worldventures.dreamtrips.wallet.ui.wizard.profile;

import com.worldventures.dreamtrips.modules.media_picker.model.PhotoPickerModel;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterI;

public interface WizardEditProfilePresenter extends WalletPresenterI<WizardEditProfileScreen> {

   void setupUserData();

   void back();

   void handlePickedPhoto(PhotoPickerModel photoPickerModel);

   void doNotAdd();

   void choosePhoto();

   void onUserDataConfirmed();
}
