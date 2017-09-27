package com.worldventures.dreamtrips.wallet.ui.wizard.profile;

import com.worldventures.dreamtrips.modules.media_picker.model.PhotoPickerModel;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;

public interface WizardEditProfilePresenter extends WalletPresenter<WizardEditProfileScreen> {

   void setupUserData();

   void back();

   void handlePickedPhoto(PhotoPickerModel photoPickerModel);

   void doNotAdd();

   void choosePhoto();

   void onUserDataConfirmed();
}
